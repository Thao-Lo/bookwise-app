package reservation.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;

import org.springframework.stereotype.Component;

@Component
public class TimeZoneConverter {
	public ZoneOffset getOffset(LocalDateTime dateTime, String userTimeZone) {
		ZoneId zoneId = ZoneId.of(userTimeZone); //Aus/Syd
		ZoneRules zoneRules = zoneId.getRules(); //DST or not
		return zoneRules.getOffset(dateTime); //+10 or +11
	}

	public LocalDateTime convertToUTC(LocalDateTime localDateTime, String userTimeZone) {
		ZoneOffset offset = getOffset(localDateTime, userTimeZone); //+10 or +11
		return localDateTime.minusSeconds(offset.getTotalSeconds()); //17:30 - 11*60*60
	}

	public LocalDateTime convertToLocalTime(LocalDateTime utcDateTime, String userTimeZone) {
		ZoneOffset offset = getOffset(utcDateTime, userTimeZone);
		return utcDateTime.plusSeconds(offset.getTotalSeconds()); //6:30 + 11*60*60
	}

	public LocalTime convertTimeToUTC(LocalDate date, LocalTime localTime, String userTimeZone) {
		LocalDateTime localDateTime = LocalDateTime.of(date, localTime);
		ZoneOffset offset = getOffset(localDateTime, userTimeZone);
		return localDateTime.minusSeconds(offset.getTotalSeconds()).toLocalTime();
	}

	public LocalTime convertTimeToLocalTime(LocalDate date, LocalTime utcTime, String userTimeZone) {
		LocalDateTime utcDateTime = LocalDateTime.of(date, utcTime);
		ZoneOffset offset = getOffset(utcDateTime, userTimeZone);
		return utcDateTime.plusSeconds(offset.getTotalSeconds()).toLocalTime();
	}

}
