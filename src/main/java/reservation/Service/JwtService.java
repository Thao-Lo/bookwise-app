package reservation.Service;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

	public String extractAccessToken (String authHeader) {
		return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
	}
	
}
