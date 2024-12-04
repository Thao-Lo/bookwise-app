package reservation.Utils;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import reservation.Repository.ScheduleRepository;
import reservation.Repository.SlotRepository;

public class CleanupTask {

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	SlotRepository slotRepository;
	
	@Scheduled(cron = "0 0 0 * * *") // Run at midnight daily
	private void cleanUpOldSchedulesAndSlots() {
		System.out.println("Running daily midnight cleanup...");
		cleanUp();
	}
	
	@EventListener(ApplicationReadyEvent.class)
	private void cleanUpOnStartUp() {
		System.out.println("Running cleanup on startup...");
		cleanUp();
	}
	private void cleanUp() {
		LocalDateTime now = LocalDateTime.now();
		
		scheduleRepository.deleteByDatetimeBefore(now);
		slotRepository.deleteByScheduleDatetimeBefore(now);
	}
}
