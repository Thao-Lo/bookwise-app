package reservation.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SlotService {
	private final List<LocalTime> generateTimeSlots = generateTimeSlots();

	private List<LocalTime> generateTimeSlots() {
		List<LocalTime> timeSlots = new ArrayList<>();

		LocalTime startTime = LocalTime.of(17, 30);
		LocalTime endTime = LocalTime.of(21, 15);

		while (startTime.isBefore(endTime)) {
			timeSlots.add(startTime);
			startTime = startTime.plusMinutes(15);
		}

		return timeSlots;
	}
}
