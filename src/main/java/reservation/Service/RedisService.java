package reservation.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import reservation.DTO.ReservationDTO;

@Service
public class RedisService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;	
	
	private static final long TTL = 300; //Time to live 5 mins 

	public String saveReservation(ReservationDTO reservationDTO) {
		//create unique number
		String sessionId = UUID.randomUUID().toString();
		String key = "reservation: " + sessionId;
		redisTemplate.opsForHash().put(key, "slotId", reservationDTO.getSlotId());
		redisTemplate.opsForHash().put(key, "tableName", reservationDTO.getTableName());
		redisTemplate.opsForHash().put(key, "date", reservationDTO.getDate());
		redisTemplate.opsForHash().put(key, "time", reservationDTO.getTime());
		redisTemplate.opsForHash().put(key, "capacity", reservationDTO.getCapacity());
		redisTemplate.expire(key, Duration.ofSeconds(TTL));
				
		return sessionId;
	}
	
	public ReservationDTO getReservation(String sessionId) {
		String key = "reservation:" + sessionId;
		
		//return type is object, alsways String -> casting
		String slotId = (String) redisTemplate.opsForHash().get(key, "slotId");
		String tableName = (String) redisTemplate.opsForHash().get(key, "tableName");
		String capacity = (String) redisTemplate.opsForHash().get(key, "capacity");
		String date = (String) redisTemplate.opsForHash().get(key, "date");
		String time = (String) redisTemplate.opsForHash().get(key, "time");
		
		
		if(slotId == null || tableName == null || date == null || time == null || capacity == null) {
			 throw new IllegalArgumentException("Session data not found or expired.");
		}
		ReservationDTO reservationDTO = new ReservationDTO();
		reservationDTO.setSessionId(sessionId);
		reservationDTO.setSlotId(Long.parseLong(slotId));
		reservationDTO.setTableName(tableName);
		reservationDTO.setCapacity(Integer.parseInt(capacity));
		reservationDTO.setDate(LocalDate.parse(date));
		reservationDTO.setTime(LocalTime.parse(time));		
		
		return reservationDTO;
	}
	public Long getRemainingTTL(String sessionId) { 
		String key = "reservation:" + sessionId;
		return redisTemplate.getExpire(key);
	}
}
