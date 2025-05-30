package reservation;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import reservation.Redis.RedisConfig;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "reservation" })
@Import(RedisConfig.class)
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		// TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
//		uncomment for EC2
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Application timezone: " + ZoneId.systemDefault());
	}
}
