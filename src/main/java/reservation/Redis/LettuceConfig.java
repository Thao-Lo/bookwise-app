package reservation.Redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@Profile("prod")
public class LettuceConfig {
	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int port;

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(hostName);
		config.setPort(port);
		System.out.println("LETTUCE REDIS HOST??? " + hostName);
		return new LettuceConnectionFactory(config);
	}
}
