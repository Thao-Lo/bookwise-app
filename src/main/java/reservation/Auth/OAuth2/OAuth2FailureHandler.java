package reservation.Auth.OAuth2;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
	@Value("${spring.profiles.active}")
	private String profile;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException{
		String protocol = profile.equalsIgnoreCase("prod") ? "https://zavism" : "http://localhost:3000";
		//error: Email is used with .... -> after encode: Email+is+used+with, no white space on URL
		String errorMessage = ((OAuth2AuthenticationException)exception).getError().getDescription();
		String redirectURL = protocol + "/oauth2/error?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
		
		response.sendRedirect(redirectURL);
	}

}
