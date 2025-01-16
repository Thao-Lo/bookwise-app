package reservation.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reservation.DTO.ReservationDTO;
import reservation.DTO.ScheduleResponse;
import reservation.DTO.SeatReservationCountDTO;
import reservation.DTO.SlotResponse;
import reservation.Entity.GuestReservation;
import reservation.Entity.GuestReservation.Status;
import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;
import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Service.GuestReservationService;
import reservation.Service.ScheduleService;
import reservation.Service.SeatService;
import reservation.Service.SlotService;
import reservation.Utils.TimeZoneConverter;

@RestController
@RequestMapping("api/v1/admin/")
@DependsOn("startRedisServer")
//@DependsOn("redissonClient")
public class AdminManagementController {
	@Autowired
	SeatService seatService;
	@Autowired
	ScheduleService scheduleService;
	@Autowired
	SlotService slotService;
	@Autowired
	GuestReservationService guestReservationService;
	@Autowired
	TimeZoneConverter timeZoneConverter;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/seats")
	public ResponseEntity<Object> getAllSeats(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		Page<Seat> seats = seatService.getAllSeat(page, size);
		if (seats.isEmpty()) {
			return new ResponseEntity<>(Map.of("Error", "No seats found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(
				Map.of("seats", seats.getContent(), "seatsPerPage", seats.getNumberOfElements(), "currentPage",
						seats.getNumber(), "totalPage", seats.getTotalPages(), "totalSeats", seats.getTotalElements()),
				HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/seats/reservation-counts")
	public ResponseEntity<Object> getResearvationCountsPerSeat(){
		try {
			List<SeatReservationCountDTO> seatReservationCountDTO = seatService.countTotalReservationsPerSeat();
			return new ResponseEntity<>(Map.of("message", "Successfully get reservation counts.", 
					"seatDataset", seatReservationCountDTO), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.OK);
		}		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/dates")
	public ResponseEntity<Object> getAllDates(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		Page<Schedule> schedulesPage = scheduleService.getAllDates(page, size);
		if (schedulesPage.isEmpty()) {
			return new ResponseEntity<>(Map.of("Error", "No Dates found"), HttpStatus.NOT_FOUND);
		}
		List<ScheduleResponse> scheduleResponses = schedulesPage.getContent().stream().map(schedule -> {
			LocalDateTime datetime = timeZoneConverter.convertToLocalTime(schedule.getDatetime(), "Australia/Sydney");
			ScheduleResponse scheduleResponse = new ScheduleResponse();
			scheduleResponse.setId(schedule.getId());
			scheduleResponse.setDate(datetime.toLocalDate());
			scheduleResponse.setTime(datetime.toLocalTime());
			return scheduleResponse;
		}).toList();

		return new ResponseEntity<>(Map.of("dates", scheduleResponses, "datesPerPage",
				schedulesPage.getNumberOfElements(), "currentPage", schedulesPage.getNumber(), "totalPage",
				schedulesPage.getTotalPages(), "totalDates", schedulesPage.getTotalElements()), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/slots")
	public ResponseEntity<Object> getAllSlots(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		Page<Slot> slotsPage = slotService.getAllSlots(page, size);
		if (slotsPage.isEmpty()) {
			return new ResponseEntity<>(Map.of("Error", "No Slots found"), HttpStatus.NOT_FOUND);
		}
		List<SlotResponse> SlotResponses = slotsPage.getContent().stream().map(slot -> {
			LocalDateTime datetime = timeZoneConverter.convertToLocalTime(slot.getSchedule().getDatetime(),
					"Australia/Sydney");
			SlotResponse slotResponse = new SlotResponse();
			slotResponse.setId(slot.getId());
			slotResponse.setTableName(slot.getSeat().getSeatName());
			slotResponse.setCapacity(slot.getSeat().getCapacity());
			slotResponse.setDate(datetime.toLocalDate());
			slotResponse.setTime(datetime.toLocalTime());
			slotResponse.setStatus(slot.getStatus().name());
			return slotResponse;
		}).toList();
		return new ResponseEntity<>(Map.of("slots", SlotResponses, "slotsPerPage", slotsPage.getNumberOfElements(),
				"currentPage", slotsPage.getNumber(), "totalPage", slotsPage.getTotalPages(), "totalSlots",
				slotsPage.getTotalElements()), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/reservations")
	public ResponseEntity<Object> getAllReservations(@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("slot.id").descending());
		Page<GuestReservation> reservationsPage = guestReservationService.getAllReservation(pageable);
		if (reservationsPage.isEmpty()) {
			return new ResponseEntity<>(Map.of("Error", "No Reservations found"), HttpStatus.NOT_FOUND);
		}
		List<ReservationDTO> reservationResponses = reservationsPage.getContent().stream().map(reservation -> {
			LocalDateTime datetime = timeZoneConverter
					.convertToLocalTime(reservation.getSlot().getSchedule().getDatetime(), "Australia/Sydney");
			ReservationDTO reservationResponse = new ReservationDTO();
			reservationResponse.setId(reservation.getId());
			reservationResponse.setTableName(reservation.getSlot().getSeat().getSeatName());
			reservationResponse.setCapacity(reservation.getSlot().getSeat().getCapacity());
			reservationResponse.setDate(datetime.toLocalDate());
			reservationResponse.setTime(datetime.toLocalTime());
			reservationResponse.setStatus(reservation.getStatus().name());
			return reservationResponse;
		}).toList();
		return new ResponseEntity<>(Map.of("reservations", reservationResponses, "reservationsPerPage",
				reservationsPage.getNumberOfElements(), "currentPage", reservationsPage.getNumber(), "totalPage",
				reservationsPage.getTotalPages(), "totalReservations", reservationsPage.getTotalElements()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/reservations/edit/{id}/{status}")
	public ResponseEntity<Object> editReservationStatus(@PathVariable Long id, @PathVariable String status) {

		if (!guestReservationService.isStatusValid(status)) {
			return new ResponseEntity<>(Map.of("error", "Invalid status provided."), HttpStatus.BAD_REQUEST);
		}
		GuestReservation reservation = guestReservationService.findReservationById(id);
		GuestReservation.Status updatedStatus = GuestReservation.Status.valueOf(status.toUpperCase());
		reservation.setStatus(updatedStatus);
		guestReservationService.updateReservationStatus(reservation);

		return new ResponseEntity<>(Map.of("message", "Successfully " + updatedStatus + " the booking"), HttpStatus.OK);
	}
}
