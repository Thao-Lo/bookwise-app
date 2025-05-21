package reservation.Redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import jakarta.annotation.PostConstruct;
@Primary
@Configuration
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
public class LettuceConfig {
	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.profiles.active:dev}")
	private String profile;
	
	@PostConstruct
	public void debugLettuceConfig() {
	    System.out.println("💡 Redis Host from @Value: " + hostName);
	}


	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(hostName);
		config.setPort(port);
		System.out.println("LETTUCE REDIS HOST??? " + hostName);
		return new LettuceConnectionFactory(config);
	}
}
