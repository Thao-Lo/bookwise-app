package reservation.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
@Autowired
private RedisTemplate<String, String> redisTemplate;
private static final long TTL = 300;

public String saveReservation(ReservationDTO reservationDTO) {
	String sessionId = UUID.randomUUID().toString();
	
}

}
