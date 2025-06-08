package reservation.Auth.OAuth2;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Service.UserService;
import reservation.Utils.JwtUtil;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	
	@Override 
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException{
		//Get authenticated OAuth2User from security context
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		//Extract email from OAuth2 Provider: Google
		String email = oAuth2User.getAttribute("email");
		System.out.println("OAUTH: INSIDE SUCCESS HANDLER");
		//find user by email
		User user = userService.findUserByEmail(email);
		
		//generate token, username == email
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
		String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name());
						
		Map<String, Object> body = Map.of("message", "Login successfully.",
				"accessToken", accessToken, 
				"refreshToken", refreshToken,
				"user" , new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name())
				);
		
		//Create sessionId and store with body in redis 
		String sessionId = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set("oauth2:sessionId:" + sessionId, body, Duration.ofMinutes(2));
		
		//write response back to client with sessionId + status 302:FOUND
		response.sendRedirect("http://localhost:3000/oauth2/success?sessionId=" + sessionId);		
	}

}
