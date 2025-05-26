package reservation.Controller;

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

import jakarta.validation.constraints.Min;
import reservation.DTO.ReservationDTO;
import reservation.DTO.ScheduleResponse;
import reservation.DTO.SeatReservationCountDTO;
import reservation.DTO.SlotResponse;
import reservation.Entity.GuestReservation;
import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;
import reservation.Service.ScheduleService;
import reservation.Service.SeatService;


@RestController
@RequestMapping("api/v1/admin/") 
public class AdminManagementController extends BaseController {
	@Autowired
	SeatService seatService;
	@Autowired
	ScheduleService scheduleService;


	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/seats")
	public ResponseEntity<Object> getAllSeats(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(10) int size) {
		Page<Seat> seats = seatService.getAllSeat(page, size);
		// from Base Controller
		checkPageNotEmpty(seats, "No seats found");

		return new ResponseEntity<>(
				createPaginationReturningData(seats, "seats", seats.getContent()),HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/seats/reservation-counts")
	public ResponseEntity<Object> getResearvationCountsPerSeat() {
		try {
			List<SeatReservationCountDTO> seatReservationCountDTO = seatService.countTotalReservationsPerSeat();
			return new ResponseEntity<>(
					Map.of("message", "Successfully get reservation counts.", "seatDataset", seatReservationCountDTO),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.OK);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/dates")
	public ResponseEntity<Object> getAllDates(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(10) int size) {
		Page<Schedule> schedulesPage = scheduleService.getAllDates(page, size);
		// from Base Controller
		checkPageNotEmpty(schedulesPage, "No Dates found");

		List<ScheduleResponse> scheduleResponses = schedulesPage.getContent().stream().map(schedule -> {
			LocalDateTime datetime = schedule.getDatetime();
			ScheduleResponse scheduleResponse = new ScheduleResponse();
			scheduleResponse.setId(schedule.getId());
			scheduleResponse.setDate(datetime.toLocalDate());
			scheduleResponse.setTime(datetime.toLocalTime());
			return scheduleResponse;
		}).toList();

		return new ResponseEntity<>(createPaginationReturningData(schedulesPage, "dates", scheduleResponses), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/slots")
	public ResponseEntity<Object> getAllSlots(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(10) int size) {
		Page<Slot> slotsPage = slotService.getAllSlots(page, size);
		// from Base Controller
		checkPageNotEmpty(slotsPage, "No Slots found");

		List<SlotResponse> SlotResponses = slotsPage.getContent().stream().map(slot -> {
			LocalDateTime datetime = slot.getSchedule().getDatetime();
			SlotResponse slotResponse = new SlotResponse();
			slotResponse.setId(slot.getId());
			slotResponse.setTableName(slot.getSeat().getSeatName());
			slotResponse.setCapacity(slot.getSeat().getCapacity());
			slotResponse.setDate(datetime.toLocalDate());
			slotResponse.setTime(datetime.toLocalTime());
			slotResponse.setStatus(slot.getStatus().name());
			return slotResponse;
		}).toList();
		return new ResponseEntity<>(createPaginationReturningData(slotsPage, "slots", SlotResponses), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/reservations")
	public ResponseEntity<Object> getAllReservations(@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(10) int size) {
		// sort by slot_id to show dates far away 1st
		Pageable pageable = PageRequest.of(page, size, Sort.by("slot.id").descending());
		
		Page<GuestReservation> reservationsPage = guestReservationService.getAllReservation(pageable);
		// from Base Controller
		checkPageNotEmpty(reservationsPage, "No Reservations found");

		List<ReservationDTO> reservationResponses = reservationsPage.getContent().stream().map(reservation -> {
			LocalDateTime datetime = reservation.getSlot().getSchedule().getDatetime();
			ReservationDTO reservationResponse = new ReservationDTO();
			reservationResponse.setId(reservation.getId());
			reservationResponse.setTableName(reservation.getSlot().getSeat().getSeatName());
			reservationResponse.setCapacity(reservation.getSlot().getSeat().getCapacity());
			reservationResponse.setDate(datetime.toLocalDate());
			reservationResponse.setTime(datetime.toLocalTime());
			reservationResponse.setStatus(reservation.getStatus().name());
			return reservationResponse;
		}).toList();
		return new ResponseEntity<>(createPaginationReturningData(reservationsPage, "reservations", reservationResponses),				
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

	private Map<String, Object> createPaginationReturningData(Page<?> page, String contentKey, Object content){
		String keyPerPage = contentKey + "PerPage";
		return Map.of(
				contentKey, content,
				keyPerPage, page.getNumberOfElements(),
				"currentPage", page.getNumber(),
				"totalPage", page.getTotalPages(),
				"totalRows", page.getTotalElements()
				);
	}
}
