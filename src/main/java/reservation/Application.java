package reservation;

import java.time.ZoneId;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		System.out.println("Application timezone: " + ZoneId.systemDefault());


	}
}
