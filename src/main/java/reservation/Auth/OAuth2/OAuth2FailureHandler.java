package reservation.Auth.OAuth2;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
	private final Environment env;
	
	public OAuth2FailureHandler(Environment env) {
		this.env = env;
	}
	private final static Logger logger = LoggerFactory.getLogger(OAuth2FailureHandler.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException{
		String profile = env.getProperty("spring.profiles.active", "dev");
		logger.info("ACTIVE PROFILE" + profile);
		
		String protocol = profile.equalsIgnoreCase("prod") ? "https://zavism.com" : "http://localhost:3000";
		//error: Email is used with .... -> after encode: Email+is+used+with, no white space on URL
		String errorMessage = ((OAuth2AuthenticationException)exception).getError().getDescription();
		String redirectURL = protocol + "/oauth2/error?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
		
		response.sendRedirect(redirectURL);
	}

}
