package reservation.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import reservation.DTO.ReservationDTO;

@Service
public class RedisService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final long TTL = 300; // Time to live 5 mins

	public String saveReservation(ReservationDTO reservationDTO) {
		// create unique number
		String sessionId = UUID.randomUUID().toString();
		// "reservation: 945f9351-e32b-495e-bcfa-4d59056d7470"
		String key = "reservation:" + sessionId;
		// "reservation: 945f9351-e32b-495e-bcfa-4d59056d7470" slotId "178"
		redisTemplate.opsForHash().put(key, "id", String.valueOf(reservationDTO.getId()));
		redisTemplate.opsForHash().put(key, "tableName", reservationDTO.getTableName());
		redisTemplate.opsForHash().put(key, "capacity", String.valueOf(reservationDTO.getCapacity()));
		redisTemplate.opsForHash().put(key, "date", reservationDTO.getDate().toString());
		redisTemplate.opsForHash().put(key, "time", reservationDTO.getTime().toString());
		redisTemplate.opsForHash().put(key, "status", "HOLDING");
		redisTemplate.expire(key, Duration.ofSeconds(TTL));
		System.out.println(
				"reservation:" + sessionId + " " + reservationDTO.getId() + " " + reservationDTO.getTableName());
		return sessionId;
	}

	public Map<String, Object> getReservation(String sessionId) {
		String key = "reservation:" + sessionId;

		// return type is object, alsways String -> casting
		String id = (String) redisTemplate.opsForHash().get(key, "id");
		String tableName = (String) redisTemplate.opsForHash().get(key, "tableName");
		String capacity = (String) redisTemplate.opsForHash().get(key, "capacity");
		String date = (String) redisTemplate.opsForHash().get(key, "date");
		String time = (String) redisTemplate.opsForHash().get(key, "time");
		String status = (String) redisTemplate.opsForHash().get(key, "status");

//	    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		if (id == null || tableName == null || capacity == null || date == null || time == null || status == null) {
			throw new IllegalArgumentException("Session data not found or expired.");
		}
		ReservationDTO reservationDTO = new ReservationDTO();
//		reservationDTO.setSessionId(sessionId);
		reservationDTO.setId(Long.parseLong(id));
		reservationDTO.setTableName(tableName);
		reservationDTO.setCapacity(Integer.parseInt(capacity));
		reservationDTO.setDate(LocalDate.parse(date));
		reservationDTO.setTime(LocalTime.parse(time));
		Map<String, Object> reservationResult = new HashMap<>();

		reservationResult.put("reservation", reservationDTO);
		reservationResult.put("status", status);
		return reservationResult;
	}

	public void setStatusToConfirming(String sessionId) {
		String key = "reservation:" + sessionId;
		String status = (String) redisTemplate.opsForHash().get(key, "status");
		if(!status.equals("HOLDING") || status == null) {
			  throw new IllegalStateException("Reservation is not in a valid state for confirmation.");
		}
		redisTemplate.opsForHash().put(key, "status", "HOLDING");
	}
	public void deleteKey(String sessionId) {
		String key = "reservation:" + sessionId;
		redisTemplate.delete(key);
	}
	public Long getRemainingTTL(String sessionId) {
		String key = "reservation:" + sessionId;
		return redisTemplate.getExpire(key);
	}

	public String dateFormatter(LocalDate localDate) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return localDate.format(dateFormatter);
	}

	public String timeFormatter(LocalTime localTime) {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return localTime.format(timeFormatter);
	}
}
