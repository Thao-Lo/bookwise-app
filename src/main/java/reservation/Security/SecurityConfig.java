package reservation.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
	 @Autowired
	    JwtAuthenticationFilter jwtAuthenticationFilter;
	 
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable()) //disable CSRF 
		//defining access control rules for different endpoints
			.authorizeHttpRequests((requests) -> requests					
					.requestMatchers("/api/v1/register","/api/v1/login", "/api/v1/verify-email", "/api/v1/user/refresh-token",  "/api/v1/slots", "/api/v1/reservation/*").permitAll() //permit multiple paths
					.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
					.requestMatchers("/api/v1/user/**").hasRole("GUEST")
					.anyRequest().authenticated() // all other endpoints require authentication	
					)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(Customizer.withDefaults()); // enable basic Authentication					
		
		return http.build();
		
	}
}
