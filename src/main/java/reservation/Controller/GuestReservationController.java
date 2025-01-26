package reservation.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.validation.constraints.Min;
import reservation.DTO.ReservationDTO;
import reservation.Entity.GuestReservation;
import reservation.Entity.User;
import reservation.Service.EmailService;
import reservation.Service.GuestReservationService;
import reservation.Service.RedisService;
import reservation.Service.SlotService;
import reservation.Service.StripeService;
import reservation.Service.UserService;
import reservation.Utils.TimeZoneConverter;

@RestController
@RequestMapping("/api/v1/")
public class GuestReservationController extends BaseController{	
	@Autowired
	private StripeService stripeService;

	final int PRICE_PER_CAPACITY = 50;
	private final static Logger logger = LoggerFactory.getLogger(GuestReservationController.class);

	private static final long TTL = 300; // Time to live 5 mins

	@PostMapping("reservation/create")
	public ResponseEntity<Object> createReservation(@RequestBody ReservationDTO reservationDTO) {
		// Use Reddison to lock the slot if it is not reserved 
		boolean isSlotLock = guestReservationService.isSlotReserve(reservationDTO.getId());
		
		if (!isSlotLock) {
			return new ResponseEntity<>(Map.of("message", "Slot is already being held by another user."),
					HttpStatus.CONFLICT);
		}
		// Mark slot from AVAILABLE to HOLDING in Db
		slotService.markSlotHolding(reservationDTO.getId());

		String sessionId = redisService.saveReservation(reservationDTO);
		logger.info("Create reservation: user Redis to lock reservation for 5 mins {}", reservationDTO);	
		
		return new ResponseEntity<>(
				Map.of("message", "Reservation is on hold", "sessionId", sessionId, "remainingTime", TTL),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('GUEST')")
	@GetMapping("/user/reservation/retrieve")
	public ResponseEntity<Object> retrieveReservation(@RequestParam String sessionId, Principal principal) {
		// accessToken, check principal == null 
		checkPrincipal(principal, "You must be logged in to retrieve your booking.");
		
		ReservationDTO reservationDTO = (ReservationDTO) redisService.getReservation(sessionId).get("reservation");
		String status = (String) redisService.getReservation(sessionId).get("status");

		logger.info("Retrieve: retrieve reservation infomation for user: {}", principal.getName());
		return new ResponseEntity<>(Map.of("reservation", reservationDTO, "remainingTime",
				redisService.getRemainingTTL(sessionId), "status", status), HttpStatus.OK);
	}

	// step 3
	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/user/reservation/create-payment")
	public ResponseEntity<Object> createPayment(@RequestParam String sessionId, Principal principal) {
		logger.info("Create Payment: create Stripe payment for sessionId: {}", sessionId);		
		String key = redisService.generateRedisKey(sessionId);
		String backupKey = redisService.generateRedisBackupKey(sessionId);

		// accessToken, check principal == null 
		checkPrincipal(principal, "You must be logged in to retrieve your booking.");
		
		// to get email
		User user = userService.findUserByUsername(principal.getName());
		// get data from redis
		Map<String, Object> reservationData = redisService.getReservation(sessionId);

		if (reservationData == null || !reservationData.containsKey("reservation")) {
			logger.warn("Create Payment: Cannot create payment because Redis session not found or expired.");
			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
		}
		try {
			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");
			//check if isSlotAvailable, not throw exception, and return
			validateSlotAvalability(reservationDTO.getId());

			long amount = (long) reservationDTO.getCapacity() * PRICE_PER_CAPACITY * 100; // convert to cents
			PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, user.getEmail(), sessionId);

			// save PaymentIntent Id to Redis reservation: and backup:
			redisTemplate.opsForHash().put(key, "paymentIntentId", paymentIntent.getId());
			redisTemplate.opsForHash().put(backupKey, "paymentIntentId", paymentIntent.getId());

			logger.info("Create Payment: create Stripe payment with paymentIntentId: {}", paymentIntent.getId());			

			return new ResponseEntity<>(Map.of("clientSecret", paymentIntent.getClientSecret(), "message",
					"Please complete your payment to confirm your booking.", "paymentIntentId", paymentIntent.getId()),
					HttpStatus.OK);
		} catch (StripeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(Map.of("error", "Payment processing failed", "details", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// step 4
	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/user/reservation/confirm-reservation")
	public ResponseEntity<Object> confirmReservation(@RequestParam String sessionId, Principal principal,
			@RequestParam String paymentIntentId) throws StripeException {
		// accessToken, check principal == null 
		checkPrincipal(principal, "You must be logged in to retrieve your booking.");
		
		Map<String, Object> reservationData = redisService.getReservation(sessionId);

		if (reservationData == null || !reservationData.containsKey("reservation")) {
			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
		}

		String storedPaymentIntentId = redisService.getPaymentIntentId(sessionId);
		if (!storedPaymentIntentId.equals(paymentIntentId)) {
			return new ResponseEntity<>(Map.of("error", "Invalid payment information."), HttpStatus.BAD_REQUEST);
		}

		try {
			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");
			//check if isSlotAvailable, not throw exception, and return
			validateSlotAvalability(reservationDTO.getId());
			
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (!"succeeded".equals(paymentIntent.getStatus())) {
				return new ResponseEntity<>(Map.of("error", "Payment not completed. Please try again."),
						HttpStatus.BAD_REQUEST);
			}
			guestReservationService.saveNewReservation(principal.getName(), reservationDTO.getId(),
					reservationDTO.getCapacity());

			// change status from HOLDING to CONFIRMING
			redisService.setStatusToConfirming(sessionId);			

			// send confirmation Email
			GuestReservation reservation = guestReservationService.findReservationBySlotId(reservationDTO.getId());
			emailService.sendBookingConfirmation(reservation);
			redisService.deleteKey(sessionId);
			redisTemplate.delete("backup:" + sessionId);
			guestReservationService.releaseRedissonLock(reservationDTO.getId());
			
			logger.info("Confirm Reservation: payment successfully, send confirmation email to: {}", reservation.getUser().getEmail());
			return new ResponseEntity<>(
					Map.of("message", "Your booking is completed. Please check your email for booking confirmation"),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					Map.of("error", "An error occurred while confirming your reservation", "details", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('GUEST')")
	@GetMapping("/user/reservation-list")
	ResponseEntity<Object> getUserReservation(@RequestParam(required = true) Long userId,
			@RequestParam(defaultValue = "0") @Min(0) Integer page,
			@RequestParam(defaultValue = "10") @Min(10) Integer size) {
	
		Page<GuestReservation> reservations = guestReservationService.getReservationByUserId(page, size, userId);
		System.out.println(reservations.getTotalElements());
		System.out.println(reservations.getTotalPages());

		if (reservations == null || reservations.isEmpty()) {
			return new ResponseEntity<>(Map.of("error", "You have no reservation yet"), HttpStatus.BAD_REQUEST);
		}
		List<ReservationDTO> reservationDTOs = reservations.stream().map(reservation -> {
			LocalDateTime localDatetime = timeZoneConverter
					.convertToLocalTime(reservation.getSlot().getSchedule().getDatetime(), "Australia/Sydney");
			return new ReservationDTO(reservation.getId(), reservation.getSlot().getSeat().getSeatName(),
					reservation.getNumberOfGuests(), localDatetime.toLocalDate(), localDatetime.toLocalTime(),
					reservation.getStatus().name());
		}).collect(Collectors.toList());
		return new ResponseEntity<>(Map.of("reservationList", reservationDTOs, "totalRows",
				reservations.getTotalElements(), "totalPages", reservations.getTotalPages()), HttpStatus.OK);
	}

	@DeleteMapping("/reservation/delete-redis-key")
	ResponseEntity<Object> getUserReservation(@RequestParam(required = true) String sessionId) throws StripeException {
		String existingKey = redisService.generateRedisKey(sessionId);
		String existingBackupKey = redisService.generateRedisBackupKey(sessionId);

		// by default, field key is object
		Map<Object, Object> rawData = redisTemplate.opsForHash().entries(existingKey);

		if (rawData == null || rawData.isEmpty()) {
			return new ResponseEntity<>(Map.of("error", "SessionId is expired or no longer exist"),
					HttpStatus.NOT_FOUND);
		}
		// casting to match the template String, Object
		Map<String, Object> reservationData = rawData.entrySet().stream()
				.collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));

		Long slotId = Long.parseLong((String) reservationData.get("id"));

		try {
			redisService.deleteKey(sessionId);
			redisTemplate.delete(existingBackupKey);
			guestReservationService.releaseRedissonLock(slotId);
			slotService.markSlotHoldingToAvailable(slotId);
			stripeService.cancelPaymentIntent((String) reservationData.get("paymentIntentId"), "duplicate");
			logger.info("Change reservation: delete Redis key, release Reddison lock and cancel payment intent for nonprocessing reservation.");
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", "Failed to perform Redis clean up", "details", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(Map.of("message", "Delete Redis key and release Redisson lock successfully."),
				HttpStatus.OK);
	}
	
	private void validateSlotAvalability(Long slotId) {		
		boolean isSlotAvailable = guestReservationService.isSlotAvailable(slotId);
		checkIsSlotAvailable(isSlotAvailable, "Slot is no longer available");		
	}
	
}
