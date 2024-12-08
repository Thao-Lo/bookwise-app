package reservation.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

@Component
public class TimeZoneConverter {

	public LocalDateTime convertToUTC(LocalDateTime localTime, String userTimeZone) {
		return localTime.atZone(ZoneId.of(userTimeZone)) //18:00 Sydney
				.withZoneSameInstant(ZoneId.of("UTC")) // 7:00 Z[UTC]
				.toLocalDateTime(); //7:00 remove Zone
	}
	public LocalDateTime convertToLocalTime(LocalDateTime utcTime, String userTimeZone) {
		return utcTime.atZone(ZoneId.of("UTC")) //7:00 UTC
				.withZoneSameInstant(ZoneId.of(userTimeZone)) // 18:00 Z[Sydney]
				.toLocalDateTime(); //18:00 remove Zone
	}
	
	public LocalTime convertTimeToUTC(LocalTime localTime, String userTimeZone) {
		LocalDate dummyDate = LocalDate.of(1970, 1, 1);
		LocalDateTime localDateTime = LocalDateTime.of(dummyDate, localTime);
		return convertToUTC(localDateTime, userTimeZone).toLocalTime();
	}
	public LocalTime convertTimeToLocalTime(LocalTime utcTime, String userTimeZone) {
		LocalDate dummyDate = LocalDate.of(1970, 1, 1);
		LocalDateTime localDateTime = LocalDateTime.of(dummyDate, utcTime);
		return convertToLocalTime(localDateTime, userTimeZone).toLocalTime();
	}	
	
}
