package reservation.Utils;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	//set the secret key by uing io.jsonwebtoken lib
	private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("e9aANUQmjLR9k767rsb0Mj39GWtyy7jg1LlBjVMDvOM=".getBytes());
	private final long ACCESS_TOKEN_VALIDITY = 3600_000;
	private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 3600_000;

	public String generateToken(String username, String role, long validity) {
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("role", role);
 
		return Jwts.builder()
//				.setClaims(claims) //custom Claims				
				.setSubject(username) //in claim
				.claim("role", role)
				.setIssuedAt(new Date()) //in claim
				.setExpiration(new Date(System.currentTimeMillis() + validity)) ////in claim
				.signWith(SECRET_KEY) // ensure it is not manipulated
				.compact();

	}

	public String generateAccessToken(String username,String role) {
		return generateToken(username, role, ACCESS_TOKEN_VALIDITY);
	}

	public String generateRefreshToken(String username, String role) {
		return generateToken(username,role, REFRESH_TOKEN_VALIDITY);
	}

	public Claims validateToken(String token) {
		try {
		return  Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody();
		}catch (JwtException e) {
			throw new IllegalArgumentException("Invalid JWT token");
		}
				
	}
	//logout, take token then calculate remaining time from now to expired time
	public Long getRemainingTokenTTL(String token) {
		Date expiration = Jwts.parserBuilder()
								.setSigningKey(SECRET_KEY)
								.build()
								.parseClaimsJws(token)
								.getBody()
								.getExpiration();
		long now = System.currentTimeMillis();
		return (expiration.getTime() - now)/1000; //calculate by sec
	}
}
//key: email and cs50 
//https://www.devglan.com/online-tools/hmac-sha256-online?ref=blog.tericcabrel.com
