package reservation.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Positive;
import reservation.DTO.SlotResponse;
import reservation.Entity.Slot;
import reservation.Enum.ErrorCode;
import reservation.Exception.SlotException;
import reservation.Service.SlotService;
import reservation.Utils.TimeZoneConverter;

@RestController
@RequestMapping("/api/v1")
public class SlotController {

	@Autowired
	SlotService slotService;
	
	@Autowired
	TimeZoneConverter timeZoneConverter;

	@GetMapping("/slots")
	public ResponseEntity<Object> getSlotByCapacity(
			@RequestParam(required=false) @Positive Integer capacity, 
			@RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
			) {
			
		
		List<Slot> slots = slotService.getSlots(capacity, date, time);
		
		if(slots.isEmpty()) {
			throw new SlotException(ErrorCode.SLOT_NOT_FOUND, String.format("No slots available for capacity: %d, date: %s, time: %s", 
			          capacity, date, time));			
		}
	
				List<SlotResponse> avaialbeSlots = slots.stream()
				.map(slot -> {
						LocalDateTime localDateTime = slot.getSchedule().getDatetime();
						return new SlotResponse(
						slot.getId(),
						slot.getSeat().getSeatName(),
						slot.getSeat().getCapacity(),
						localDateTime.toLocalDate(),
						localDateTime.toLocalTime(),
						slot.getStatus().name()
						);
				})
				.collect(Collectors.toList());
				
					
		return new ResponseEntity<>(Map.of("availableSlots", avaialbeSlots), HttpStatus.OK);
	}	
}

