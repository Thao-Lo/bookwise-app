package reservation.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import reservation.DTO.ReservationDTO;
import reservation.Entity.GuestReservation;
import reservation.Entity.User;
import reservation.Service.EmailService;
import reservation.Service.GuestReservationService;
import reservation.Service.RedisService;
import reservation.Service.UserService;

@RestController
@RequestMapping("/api/v1/")
public class GuestReservationController {
	@Autowired
	private RedisService redisService;
	@Autowired
	GuestReservationService guestReservationService;
	@Autowired
	EmailService emailService;
	@Autowired
	UserService userService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	final int PRICE_PER_CAPACITY = 50;

	private static final long TTL = 300; // Time to live 5 mins

	@PostMapping("reservation/create")
	public ResponseEntity<Object> createReservation(@RequestBody ReservationDTO reservationDTO) {
		String slotKey = "slot:" + reservationDTO.getId();

		boolean isSlotLock = guestReservationService.isSlotReserve(slotKey);

		if (!isSlotLock) {
			return new ResponseEntity<>(Map.of("message", "Slot is already being held by another user."),
					HttpStatus.CONFLICT);
		}

		String sessionId = redisService.saveReservation(reservationDTO);

		System.out.println("reservation DTO" + reservationDTO);
		return new ResponseEntity<>(
				Map.of("message", "Reservation is on hold", "sessionId", sessionId, "remainingTime", TTL),
				HttpStatus.OK);
	}
	@PreAuthorize("hasRole('GUEST')")
	@GetMapping("/user/reservation/retrieve")
	public ResponseEntity<Object> retrieveReservation(@RequestParam String sessionId, Principal principal) {
		// accessToken
		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."),
					HttpStatus.OK);
		}
		System.out.println("principal name: " + principal.getName());
		ReservationDTO reservationDTO = (ReservationDTO) redisService.getReservation(sessionId).get("reservation");
		String status = (String) redisService.getReservation(sessionId).get("status");

		return new ResponseEntity<>(Map.of("reservation", reservationDTO, "remainingTime",
				redisService.getRemainingTTL(sessionId), "status", status), HttpStatus.OK);
	}

	// step 3
	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/user/reservation/create-payment")
	public ResponseEntity<Object> createPayment(@RequestParam String sessionId, Principal principal) {
		System.out.println("sessionId payment: " + sessionId);
		String key = "reservation:" + sessionId;
		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."),
					HttpStatus.UNAUTHORIZED);
		}		
		// to get email
		User user = userService.findUserByUsername(principal.getName());
		// get data from redis
		Map<String, Object> reservationData = redisService.getReservation(sessionId);

		if (reservationData == null || !reservationData.containsKey("reservation")) {
			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
		}

		try {
			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");

			boolean isSlotAvailable = guestReservationService.isSlotAvailable(reservationDTO.getId());

			if (!isSlotAvailable) {
				return new ResponseEntity<>(Map.of("error", "Slot is no longer available"), HttpStatus.NOT_FOUND);
			}
			
			long amount = (long) reservationDTO.getCapacity() * PRICE_PER_CAPACITY * 100; // convert to cents
			PaymentIntent paymentIntent = PaymentIntent
					.create(PaymentIntentCreateParams
							.builder()
							.setAmount(amount)
							.setCurrency("aud")
							.setReceiptEmail(user.getEmail())
							.putMetadata("sessionId", sessionId)
							.build());		
			redisTemplate.opsForHash().put(key, "paymentIntentId", paymentIntent.getId());
			System.out.println("paymentIntentId" + paymentIntent.getId());			
			
			return new ResponseEntity<>(Map.of("clientSecret", paymentIntent.getClientSecret(),
					"message","Please complete your payment to confirm your booking.",					
					"paymentIntentId", paymentIntent.getId() ), HttpStatus.OK);
		} catch (StripeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(Map.of("error", "Payment processing failed", "details", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// step 4
	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/user/reservation/confirm-reservation")
	public ResponseEntity<Object> confirmReservation(@RequestParam String sessionId, Principal principal, @RequestParam String paymentIntentId) throws StripeException {
		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."),
					HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> reservationData = redisService.getReservation(sessionId);

		if (reservationData == null || !reservationData.containsKey("reservation")) {
			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
		}
		
		String storedPaymentIntentId = redisService.getPaymentIntentId(sessionId);
		if(!storedPaymentIntentId.equals(paymentIntentId)) {
			return new ResponseEntity<>(Map.of("error", "Invalid payment information."), HttpStatus.BAD_REQUEST);
		}		
		
		try {
			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");

			boolean isSlotAvailable = guestReservationService.isSlotAvailable(reservationDTO.getId());

			if (!isSlotAvailable) {
				return new ResponseEntity<>(Map.of("error", "Slot is no longer available"), HttpStatus.NOT_FOUND);
			}
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
			
			if(!"succeeded".equals(paymentIntent.getStatus())) {
				return new ResponseEntity<>(Map.of("error", "Payment not completed. Please try again."),
	                    HttpStatus.BAD_REQUEST);
			}
			guestReservationService.saveNewReservation(principal.getName(), reservationDTO.getId(),
					reservationDTO.getCapacity());
			
			// change status from HOLDING to CONFIRMING
			redisService.setStatusToConfirming(sessionId);

			// send confirmation Email
			GuestReservation reservation = guestReservationService.findReservationById(reservationDTO.getId());
			emailService.sendBookingConfirmation(reservation);
			redisService.deleteKey(sessionId);

			return new ResponseEntity<>(
					Map.of("message", "Your booking is completed. Please check your email for booking confirmation"),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(Map.of("error", "An error occurred while confirming your reservation","details", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@PostMapping("/user/reservation/confirm")
//	public ResponseEntity<Object> confirmReservation1(@RequestParam String sessionId, Principal principal) {
//		if (principal == null) {
//			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."),
//					HttpStatus.UNAUTHORIZED);
//		}
//		Map<String, Object> reservationData = redisService.getReservation(sessionId);
//
//		if (reservationData == null || !reservationData.containsKey("reservation")) {
//			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
//		}
//		
//		// change status from HOLDING to CONFIRMING
//		redisService.setStatusToConfirming(sessionId);
//		
//		try {			
//			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");
//			
//			boolean isSlotAvailable = guestReservationService.isSlotAvailable(reservationDTO.getId());
//			
//			if (!isSlotAvailable) {
//				return new ResponseEntity<>(Map.of("error", "Slot is no longer available"), HttpStatus.NOT_FOUND);
//			}
//			
//			guestReservationService.saveNewReservation(principal.getName(), reservationDTO.getId(),
//					reservationDTO.getCapacity());
//			
//			//send confirmation Email
//			GuestReservation reservation = guestReservationService.findReservationById(reservationDTO.getId());
//			emailService.sendBookingConfirmation(reservation);
//		} finally {
//			redisService.deleteKey(sessionId);
//		}
//
//		return new ResponseEntity<>(Map.of("message", "Your booking is completed. Please check your email for booking confirmation"), HttpStatus.OK);
//	}
}
