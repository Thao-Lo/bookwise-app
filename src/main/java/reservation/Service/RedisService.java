package reservation.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
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
		redisTemplate.opsForHash().put(key, "status", reservationDTO.getStatus());
		redisTemplate.opsForHash().put(key, "bookingStatus", "HOLDING");
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
		String bookingStatus = (String) redisTemplate.opsForHash().get(key, "bookingStatus");

//	    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		if (id == null || tableName == null || capacity == null || date == null || time == null || status == null || bookingStatus == null) {
			throw new IllegalArgumentException("Session data not found or expired.");
		}
		ReservationDTO reservationDTO = new ReservationDTO();
//		reservationDTO.setSessionId(sessionId);
		reservationDTO.setId(Long.parseLong(id));
		reservationDTO.setTableName(tableName);
		reservationDTO.setCapacity(Integer.parseInt(capacity));
		reservationDTO.setDate(LocalDate.parse(date));
		reservationDTO.setTime(LocalTime.parse(time));
		reservationDTO.setStatus(status);
		Map<String, Object> reservationResult = new HashMap<>();

		reservationResult.put("reservation", reservationDTO);
		reservationResult.put("status", bookingStatus);
		return reservationResult;
	}
	//get paymentIntend Id for stripe
	public String getPaymentIntentId(String sessionId) {
		String key = "reservation:" + sessionId;
		String id = (String) redisTemplate.opsForHash().get(key, "paymentIntentId");	
		if (id == null) {
			throw new IllegalArgumentException("Session data not found or expired.");
		}
		return id;
	}
	
	public void setStatusToConfirming(String sessionId) {
		String key = "reservation:" + sessionId;
		String bookingStatus = (String) redisTemplate.opsForHash().get(key, "bookingStatus");
		if(!bookingStatus.equals("HOLDING") || bookingStatus == null) {
			  throw new IllegalStateException("Reservation is not in a valid state for confirmation.");
		}
		redisTemplate.opsForHash().put(key, "bookingStatus", "CONFIRMING");
	}
	public void deleteKey(String sessionId) {
		String key = "reservation:" + sessionId;
		redisTemplate.delete(key);
	}
	public Long getRemainingTTL(String sessionId) {
		String key = "reservation:" + sessionId;
		return redisTemplate.getExpire(key);
	}
	
	//logout
	//use opsForValue for <String, String> (key, value, duration)
	public void blacklistToken(String token, long ttl) {
	    redisTemplate.opsForValue().set("blacklist:" + token, "blacklisted", Duration.ofSeconds(ttl));
	}
	//logout
	public boolean isTokenBlacklist(String token) {
		return redisTemplate.hasKey("blacklist:" + token);
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
