package reservation.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reservation.DTO.ReservationDTO;
import reservation.Service.RedisService;

@RestController
@RequestMapping("/api/v1/reservation")
public class GuestReservationController {
	@Autowired
	private RedisService redisService;
	
	private static final long TTL = 300; // Time to live 5 mins

	@PostMapping("/create")
	public ResponseEntity<Object> createReservation(@RequestBody ReservationDTO reservationDTO){
		String sessionId = redisService.saveReservation(reservationDTO);
		System.out.println("reservation DTO" + reservationDTO);
		return new ResponseEntity<>
		(Map.of("message", "Reservation is on hold",
				"sessionId", sessionId,
				"remainingTime", TTL), HttpStatus.OK);		
	}
	
	@GetMapping("/retrieve")
	public ResponseEntity<Object> retrieveReservation(@RequestParam String sessionId, Principal principal){
		//accessToken
		if(principal == null) {
			return new ResponseEntity<>(Map.of("error", "You must be logged in to retrieve your booking."), HttpStatus.OK);		
		}
		ReservationDTO reservationDTO = redisService.getReservation(sessionId);
		
	return new ResponseEntity<>
		(Map.of("reservation", reservationDTO,"remainingTime", redisService.getRemainingTTL(sessionId)), HttpStatus.OK);		
	}
	
	
}
