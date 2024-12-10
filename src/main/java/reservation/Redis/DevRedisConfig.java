package reservation.Redis;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevRedisConfig {
	@Bean
	public Process startRedisServer() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("C:\\Users\\Zavis\\Desktop\\PROJECT\\Redis\\redis-server.exe");
			Process process = processBuilder.start();
			System.out.println("Redis Server started in development mode");
			return process;
		}catch (IOException e){
			throw new RuntimeException("Failed to start Redis Server", e);
		}
	}
}
