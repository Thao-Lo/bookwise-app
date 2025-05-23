package reservation.Redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import jakarta.annotation.PostConstruct;

@Configuration
//@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class LettuceConfig {
	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.profiles.active:dev}")
	private String profile;
	static {
	    System.out.println("LettuceConfig CLASS LOADED");
	}
	@PostConstruct
	public void debugLettuceConfig() {
	    System.out.println("Redis Host from @Value: " + hostName);
	}
	@Primary
	@Bean(name = "customLettuceConnectionFactory" )
	public LettuceConnectionFactory customLettuceConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(hostName);
		config.setPort(port);
		System.out.println("LETTUCE REDIS HOST??? " );
		return new LettuceConnectionFactory(config);
	}
	
	   
}
