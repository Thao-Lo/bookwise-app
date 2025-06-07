package reservation.Auth.OAuth2;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
		
		//prepare HTTP response: json and status
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK); //200
		
		//JSON String
		String body = new ObjectMapper().writeValueAsString(Map.of(
				"message", "Login successfully.",
				"accessToken", accessToken, 
				"refreshToken", refreshToken,
				"user" , new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name())
				));
		
		//write response back to client
		response.getWriter().write(body);
	}

}
