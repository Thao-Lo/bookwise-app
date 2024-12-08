package reservation.Utils;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import reservation.Repository.ScheduleRepository;
import reservation.Repository.SlotRepository;

//@Component
public class CleanupTask {

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	SlotRepository slotRepository;
	
	@Autowired
	TimeZoneConverter timeZoneConverter;
	
	@Scheduled(cron = "0 0 0 * * *") // Run at midnight daily
	private void cleanUpOldSchedulesAndSlots() {
		System.out.println("Running daily midnight cleanup...");
		cleanUp();
	}	
	
	@EventListener(ApplicationReadyEvent.class)
	@Order(1)
	public void cleanUpOnStartUp() {
		System.out.println("Running cleanup on startup...");
		cleanUp();
	}
	
	public void cleanUp() {
		LocalDateTime now = timeZoneConverter.convertToUTC(LocalDateTime.now(), "Australia/Sydney");		
		scheduleRepository.deleteByDatetimeBefore(now);
		slotRepository.deleteByScheduleDatetimeBefore(now);
	}
}
