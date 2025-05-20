package reservation.Redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class LettuceConfig {
	@Value("${spring.redis.host}")
	private String hostName;
	
	@Value("${spring.redis.port}")
	private String port;
	

	@Bean 
	public LettuceConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(null);
		config.setPort(0);
		return new LettuceConnectionFactory(config);
	}
	
	
}
