package reservation.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer{
	 @Autowired
	    JwtAuthenticationFilter jwtAuthenticationFilter;
	 
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable()) //disable CSRF 
		//defining access control rules for different endpoints
			.authorizeHttpRequests((requests) -> requests					
//					.requestMatchers("/api/v1/register","/api/v1/login", "/api/v1/verify-email",  "/api/v1/slots", "/api/v1/reservation/create").permitAll() //permit multiple paths
					.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
					.requestMatchers("/api/v1/user/**").hasRole("GUEST")
					.anyRequest().permitAll() // all other endpoints require authentication	
					)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(Customizer.withDefaults()); // enable basic Authentication					
		
		return http.build();
		
	}	
	
	public void addCorsMappings(CorsRegistry registry) {
		// Allow cross-origin requests from localhost:3000
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000/")
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("*")
				.allowCredentials(true); // Allow credentials like cookies
	}
}
