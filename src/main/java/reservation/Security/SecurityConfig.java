package reservation.Security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import reservation.Auth.OAuth2.CustomOAuth2UserService;
import reservation.Auth.OAuth2.OAuth2FailureHandler;
import reservation.Auth.OAuth2.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired 
	CustomOAuth2UserService customOAuth2UserService;
	
	@Autowired
	OAuth2SuccessHandler oAuth2SuccessHandler;
	@Autowired 
	OAuth2FailureHandler oAuth2FailureHandler;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // disable CSRF
				// defining access control rules for different endpoints
				.cors(Customizer.withDefaults()) // Enable CORS with default config
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/api/v1/user/profile").hasAnyRole("GUEST", "ADMIN")
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/user/**").hasRole("GUEST")						
						.anyRequest().permitAll() // all other endpoints require authentication
				)
				.oauth2Login(oauth -> oauth
						.userInfoEndpoint(userInfor -> userInfor.userService(customOAuth2UserService))
						.successHandler(oAuth2SuccessHandler)
						.failureHandler(oAuth2FailureHandler)
						)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
				

		return http.build();

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://my-bookwise-app.s3-website-ap-southeast-2.amazonaws.com","https://zavism.com", "http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
