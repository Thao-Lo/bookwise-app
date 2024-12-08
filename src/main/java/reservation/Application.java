package reservation;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Application timezone: " + ZoneId.systemDefault());
	}
}
