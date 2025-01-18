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

import reservation.DTO.ReservationDTO;

@Service
public class RedisService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final long TTL = 300; // Time to live 5 mins
	// Centralized Error Message
	private static final String SESSION_NOT_FOUND = "Session data not found or expired.";
	private static final String INVALID_RESERVATION_STATE = "Reservation is not in a valid state for confirmation.";


	public String saveReservation(ReservationDTO reservationDTO) {
		// create unique number
		String sessionId = UUID.randomUUID().toString();
		// "reservation: 945f9351-e32b-495e-bcfa-4d59056d7470"
		//Primary key
		String key = generateRedisKey(sessionId);
		
		//Backup key
		String backupKey = generateRedisBackupKey(sessionId);
				
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
		
		//Save backup data to Redis
		redisTemplate.opsForHash().put(backupKey, "slotId", String.valueOf(reservationDTO.getId()));
		redisTemplate.expire(backupKey, Duration.ofSeconds(TTL + 1));
		
		return sessionId;
	}

	public Map<String, Object> getReservation(String sessionId) {
		String key = generateRedisKey(sessionId);

		// return type is object, alsways String -> casting
		String id = getHashValue(key, "id");
		String tableName = getHashValue(key, "tableName");
		String capacity = getHashValue(key, "capacity");
		String date = getHashValue(key, "date");
		String time = getHashValue(key, "time");
		String status = getHashValue(key, "status");
		String bookingStatus = getHashValue(key, "bookingStatus");

//	    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		if (id == null || tableName == null || capacity == null || date == null || time == null || status == null || bookingStatus == null) {
			throw new IllegalArgumentException(SESSION_NOT_FOUND);
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
	// centralized function to create Redis key
	public String generateRedisKey(String sessionId) {
		return "reservation:" + sessionId;
	}	
	// centralized function to create Redis backup key
	public String generateRedisBackupKey(String sessionId) {
		return "backup:" + sessionId;
	}
	// centralized Redis get hash Value
	public String getHashValue (String key, String field) {
		return (String) redisTemplate.opsForHash().get(key, field);
	}	
	
	//get paymentIntend Id for stripe
	public String getPaymentIntentId(String sessionId) {		
		String id = getHashValue(generateRedisKey(sessionId), "paymentIntentId");	
		if (id == null) {
			throw new IllegalArgumentException(SESSION_NOT_FOUND);
		}
		return id;
	}
	
	public void setStatusToConfirming(String sessionId) {
		String bookingStatus = (String) redisTemplate.opsForHash().get(generateRedisKey(sessionId), "bookingStatus");
		if(!bookingStatus.equals("HOLDING") || bookingStatus == null) {
			  throw new IllegalStateException(INVALID_RESERVATION_STATE);
		}
		redisTemplate.opsForHash().put(generateRedisKey(sessionId), "bookingStatus", "CONFIRMING");
	}
	public void deleteKey(String sessionId) {
		redisTemplate.delete(generateRedisKey(sessionId));
	}
		
	public Long getRemainingTTL(String sessionId) {		
		return redisTemplate.getExpire(generateRedisKey(sessionId));
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
//	public boolean deleteSlotKey(String slotId) {
//	    // Key dành cho RLock
//	    String lockKey = "lock:slot:" + slotId;
//
//	    // Key dành cho RBucket
//	    String reservationKey = "reservation:slot:" + slotId;
//
//	    // Xử lý RLock
//	    RLock lock = redissonClient.getLock(lockKey);
//	    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//	        lock.unlock();
//	    }
//
//	    // Xử lý RBucket
//	    RBucket<Object> redisKey = redissonClient.getBucket(reservationKey);
//	    if (redisKey.isExists()) {
//	        redisKey.delete();
//	        System.out.println("Deleted Redis key: " + reservationKey);
//	        return true;
//	    } else {
//	        System.out.println("Redis key does not exist: " + reservationKey);
//	        return false;
//	    }
//	}

}
