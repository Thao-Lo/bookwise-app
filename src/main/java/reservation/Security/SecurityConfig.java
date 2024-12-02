package reservation.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable()) //disable CSRF 
			.authorizeHttpRequests((requests) -> requests					
					.requestMatchers("/api/v1/register").permitAll() //permit multiple paths
					.anyRequest().authenticated() // all other endpoints require authentication
					)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(Customizer.withDefaults()); // enable basic Authentication					
		
		return http.build();
		
	}
}
