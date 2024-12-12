package reservation.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reservation.DTO.ReservationDTO;
import reservation.Service.GuestReservationService;
import reservation.Service.RedisService;

@RestController
@RequestMapping("/api/v1/")
public class GuestReservationController {
	@Autowired
	private RedisService redisService;
	@Autowired
	GuestReservationService guestReservationService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

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

	@PostMapping("/user/reservation/confirm")
	public ResponseEntity<Object> confirmReservation(@RequestParam String sessionId, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."),
					HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> reservationData = redisService.getReservation(sessionId);

		if (reservationData == null || !reservationData.containsKey("reservation")) {
			return new ResponseEntity<>(Map.of("error", "Session not found or expired"), HttpStatus.BAD_REQUEST);
		}
		// change status from HOLDING to CONFIRMING
		redisService.setStatusToConfirming(sessionId);
		try {
			ReservationDTO reservationDTO = (ReservationDTO) reservationData.get("reservation");
			boolean isSlotAvailable = guestReservationService.isSlotAvailable(reservationDTO.getId());
			if (!isSlotAvailable) {
				return new ResponseEntity<>(Map.of("error", "Slot is no longer available"), HttpStatus.NOT_FOUND);
			}
			guestReservationService.saveNewReservation(principal.getName(), reservationDTO.getId(),
					reservationDTO.getCapacity());
		} finally {
			redisService.deleteKey(sessionId);
		}

		return new ResponseEntity<>(Map.of("message", "Your booking is completed"), HttpStatus.OK);
	}
}
