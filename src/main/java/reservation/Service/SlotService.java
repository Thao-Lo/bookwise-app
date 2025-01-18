package reservation.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;
import reservation.Entity.Slot.Status;
import reservation.Repository.ScheduleRepository;
import reservation.Repository.SeatRepository;
import reservation.Repository.SlotRepository;
import reservation.Utils.TimeZoneConverter;

@Service
public class SlotService {
	@Autowired
	SeatRepository seatRepository;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	SlotRepository slotRepository;

	@Autowired
	TimeZoneConverter timeZoneConverter;
	
	private final Slot.Status AVAILABLE = Slot.Status.AVAILABLE;
	private final Slot.Status HOLDING = Slot.Status.HOLDING;

	public Page<Slot> getAllSlots(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return slotRepository.findAll(pageable);
	}

	public void markSlotHoldingToAvailable(Long slotId) {
		Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
		if (slot != null && slot.getStatus() == HOLDING) {
			slot.setStatus(AVAILABLE);
			slotRepository.save(slot);
		}
	}

	public void markSlotHolding(Long slotId) {
		Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
		if (slot != null && slot.getStatus() == AVAILABLE) {
			slot.setStatus(HOLDING);
			slotRepository.save(slot);
			return;
		}
		throw new IllegalArgumentException("Slot is not available for booking");
	}

	public void generateSlots() {
		LocalDateTime datetime = LocalDateTime.now();
		List<Schedule> schedules = scheduleRepository.getAllDatetimeAfterToday(datetime);
		List<Seat> seats = seatRepository.findAll();
		List<Slot> slots = new ArrayList<>();

		// only get the existing slots in db
		List<Slot> existingSlots = slotRepository.existingSlots(schedules, seats);
		// from existing slot, create key for each slots and store in set
		Set<String> existingSlotKeys = existingSlots.stream()
				.map(slot -> slot.getSchedule().getId() + "_" + slot.getSeat().getId()).collect(Collectors.toSet());

		for (Schedule schedule : schedules) {
			for (Seat seat : seats) {
				String key = schedule.getId() + "_" + seat.getId(); // create compare key with key in SET O1
				if (!existingSlotKeys.contains(key)) {
					Slot slot = new Slot();
					slot.setSeat(seat);
					slot.setSchedule(schedule);
					slot.setStatus(AVAILABLE);
					slots.add(slot);
				}
			}
		}
		slotRepository.saveAll(slots);
	}

	public boolean isSlotsExist(Seat seat, Schedule schedule) {
		return slotRepository.existsBySeatAndSchedule(seat, schedule);
	}

	public List<Slot> getSlotsbySeatCapacity(int capacity) {
		return slotRepository.getSlotsBySeatCapacity(capacity);
	}

	public List<Slot> getSlots(Integer capacity, LocalDate date, LocalTime time) {

		int[] capacities = { 2, 4, 6 };

		// Default capacity if null
		if (capacity == null) {
			capacity = capacities[0]; // Default to smallest capacity
		}

		// Normalize capacity
		capacity = normalizeCapacity(capacity, capacities);
		if (capacity == null) {
			return Collections.emptyList(); // Invalid capacity
		}

		// Case 1: All parameters are null
		if (date == null && time == null) {
			return slotRepository.getSlotsBySeatCapacity(capacity);
		}

		// Case 2: time == null && date != null
		if (time == null) {
			return slotRepository.getSlotsBySeatCapacityAndDate(capacity, date);
		}

		// Case 3: time != null && date == null
		if (date == null && time != null) {
			LocalDate today = LocalDate.now();
			List<LocalDate> dates = today.datesUntil(today.plusDays(30)).collect(Collectors.toList());
			List<Slot> availableSlots = new ArrayList<>();

			for (LocalDate searchDate : dates) {
				LocalTime utcTime = timeZoneConverter.convertTimeToUTC(searchDate, time, "Australia/Sydney");
				System.out.println("UTC time for date " + searchDate + ": " + utcTime);
				LocalDateTime localDateTime = LocalDateTime.of(searchDate, utcTime);
				List<Slot> slots = slotRepository.getSlotBySeatCapacityAndTime(capacity, localDateTime);
				if (slots != null) {
					availableSlots.addAll(slots);
				}
			}

			return availableSlots;
		}
		// Case 4: All parameters are provided
		if (date != null && time != null) {
			LocalTime utcTime = timeZoneConverter.convertTimeToUTC(date, time, "Australia/Sydney");
			System.out.println("Converted time to UTC: " + utcTime);
			return slotRepository.getSlotsBySeatCapacityAndDateAndTime(capacity, date, utcTime);
		}

		// Fallback (should not occur)
		return Collections.emptyList();

	}

	private Integer normalizeCapacity(Integer capacity, int[] capacities) {
		for (int i = 0; i < capacities.length; i++) {
			if (capacity <= capacities[i]) {
				return capacities[i];
			}
		}
		return null; // Return null if capacity exceeds maximum
	}
}
//private final List<LocalTime> generateTimeSlots = generateTimeSlots();
//
//private List<LocalTime> generateTimeSlots() {
//	List<LocalTime> timeSlots = new ArrayList<>();
//
//	LocalTime startTime = LocalTime.of(17, 30);
//	LocalTime endTime = LocalTime.of(21, 15);
//
//	while (startTime.isBefore(endTime)) {
//		timeSlots.add(startTime);
//		startTime = startTime.plusMinutes(15);
//	}
//
//	return timeSlots;
//}