package reservation.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reservation.Entity.Seat;
import reservation.Service.GuestReservationService;
import reservation.Service.ScheduleService;
import reservation.Service.SeatService;
import reservation.Service.SlotService;

@RestController
@RequestMapping("api/v1/admin/")
public class AdminManagementController {
	@Autowired
	SeatService seatService;
	@Autowired
	ScheduleService sheduleService;
	@Autowired
	SlotService slotService;
	
	
	@PreAuthorize("hasRole('ADMIN')") 
	@GetMapping("/seats")
	public ResponseEntity<Object> getAllSeats(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		Page<Seat> seats = seatService.getAllSeat(page, size);
		if (seats.isEmpty()) {
			return new ResponseEntity<>(Map.of("Error", "No seats found"), HttpStatus.NOT_FOUND);
		}		
		return new ResponseEntity<>(Map.of(
				"seats", seats.getContent(),
				"seatsPerPage", seats.getNumberOfElements(),
				"currentPage", seats.getNumber(),
				"totalPage", seats.getTotalPages(),
				"totalSeats", seats.getTotalElements()
				), HttpStatus.OK);
	}

}
