package reservation.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import reservation.Service.ScheduleService;
import reservation.Service.SlotService;


@Component
public class ScheduleInitializer {

	@Autowired
	ScheduleService scheduleService;

	@Autowired
	SlotService slotService;
	
	@EventListener(ApplicationReadyEvent.class)
	@Order(2)
	public void initializeSchedules() {
		System.out.println("Initializing schedules for the next 30 days..");
		scheduleService.insertScheduleForNext30Days();		
		System.out.println("Schedule initialization completed.");
	}
	
	@EventListener(ApplicationReadyEvent.class)
	@Order(3)
	public void initializeSlots() {
		System.out.println("Initializing slots for the next 30 days..");
		slotService.generateSlots();
		System.out.println("Slots initialization completed.");
	}

}
