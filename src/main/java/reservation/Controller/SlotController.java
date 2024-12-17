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
			return new ResponseEntity<>(Map.of("error", String.format("No slots available for capacity: %d, date: %s, time: %s", 
			          capacity, date, time)), HttpStatus.NOT_FOUND);
		}
	
				List<SlotResponse> avaialbeSlots = slots.stream()
				.map(slot -> {
						LocalDateTime localDateTime = timeZoneConverter.convertToLocalTime(slot.getSchedule().getDatetime(), "Australia/Sydney");
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


//@GetMapping("/slot/{capacity}")
//public ResponseEntity<Object> getSlotByCapacity(@PathVariable @Positive int capacity) {		
//	List<Slot> slots = slotService.getSlotsbySeatCapacity(capacity);
//	if(slots.isEmpty()) {
//		return new ResponseEntity<>(Map.of("error", "No slots available for the given capacity"), HttpStatus.NOT_FOUND);
//	}
////	List<Map<String, Object>> avaialbeSlots = slots.stream()   .map(slot -> Map.of(
//   //"Table name", slot.getSeat().getSeatName(), 
//	
//	// Will map out SlotResponse to JSON 
//			List<SlotResponse> avaialbeSlots = slots.stream()
//			.map(slot -> new SlotResponse(
//					slot.getSeat().getSeatName(),
//					slot.getSeat().getCapacity(),
//					timeZoneConverter.convertToLocalTime(slot.getSchedule().getDatetime(), "Australia/Sydney")					
//					))
//			.collect(Collectors.toList());
//			
//				
//	return new ResponseEntity<>(Map.of("availableSlots", avaialbeSlots), HttpStatus.OK);
//}