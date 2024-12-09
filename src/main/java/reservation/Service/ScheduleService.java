package reservation.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reservation.Entity.Schedule;
import reservation.Repository.ScheduleRepository;

@Service
public class ScheduleService {
	@Autowired
	ScheduleRepository scheduleRepository;

	public void insertScheduleForNext30Days() {
		//today: date + time
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		List<Schedule> schedules = new ArrayList<>();
		List<LocalDateTime> datetimeInDb = scheduleRepository.findAllDatetimeBetween(now, now.plusDays(30));
		Set<LocalDateTime> datetimeInDbSet = new HashSet<>(datetimeInDb);
		
		for (int i = 0; i < 30; i++) {
			//extract date only then increase by i
			LocalDate date = now.toLocalDate().plusDays(i);
			//start from 17:30
			LocalTime startTime = LocalTime.of(17, 30);
			//end at 21:15
			LocalTime endTime = LocalTime.of(21, 15);
			
			//end before 21:15 -> 21:00
			while (startTime.isBefore(endTime)) {
				
				//add current date with each time slots
				LocalDateTime datetime = LocalDateTime.of(date, startTime);
				if(date.equals(today) && datetime.isBefore(now)) {
					startTime = startTime.plusMinutes(60); // Move to the next slot
	                continue;
				}
				//check whether db already have this daytime
				if(!datetimeInDbSet.contains(datetime)) {
					schedules.add(new Schedule(datetime));
				}
				
				startTime = startTime.plusMinutes(60);
			}
		}
		System.out.println( schedules);
		//save whole batch
		 if (!schedules.isEmpty()) {
	            scheduleRepository.saveAll(schedules);
	        }
	}
}
