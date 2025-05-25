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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
//	public class SecurityConfig implements WebMvcConfigurer{
	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // disable CSRF
				// defining access control rules for different endpoints
				.cors(Customizer.withDefaults()) // Enable CORS with default config
				.authorizeHttpRequests((requests) -> requests
//					.requestMatchers("/api/v1/register","/api/v1/login", "/api/v1/verify-email",  "/api/v1/slots", "/api/v1/reservation/create").permitAll() //permit multiple paths
						.requestMatchers("/api/v1/login","/api/v1/register").permitAll()
						.requestMatchers("/api/v1/user/profile").hasAnyRole("GUEST", "ADMIN")
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/user/**").hasRole("GUEST")						
//						.anyRequest().permitAll() // all other endpoints require authentication
				).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//				.httpBasic(Customizer.withDefaults()); // enable basic Authentication

		return http.build();

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://my-bookwise-app.s3-website-ap-southeast-2.amazonaws.com","https://zavism.com", "http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

//	public void addCorsMappings(CorsRegistry registry) {
//		// Allow cross-origin requests from localhost:3000
//		registry.addMapping("/**")
//				.allowedOrigins("http://localhost:3000")
//				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//				.allowedHeaders("Authorization", "Content-Type")
//				.allowCredentials(true); // Allow credentials like cookies
//	}
}
