package reservation.Utils;

import java.time.LocalDateTime;
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
}
