package reservation.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;
import reservation.Repository.ScheduleRepository;
import reservation.Repository.SeatRepository;
import reservation.Repository.SlotRepository;

@Service
public class SlotService {
	@Autowired
	SeatRepository seatRepository;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	SlotRepository slotRepository;

	public void generateSlots() {
		List<Schedule> schedules = scheduleRepository.findAll();
		List<Seat> seats = seatRepository.findAll();
		List<Slot> slots = new ArrayList<>();

		for (Schedule schedule : schedules) {
			for (Seat seat : seats) {
				Slot slot = new Slot();
				slot.setSeat(seat);
				slot.setSchedule(schedule);
				slot.setStatus(Slot.Status.AVAILABLE);
				slots.add(slot);
			}
		}
		slotRepository.saveAll(slots);
	}
	public List<Slot> getSlotsbySeatCapacity(int capacity){
		return slotRepository.getSlotsBySeatCapacity(capacity);
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