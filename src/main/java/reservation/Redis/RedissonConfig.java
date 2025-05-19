package reservation.Redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	// distributed storage: lock, cache,.
	@Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.profiles.active:dev}")
	private String profile;

	@Bean
	public RedissonClient redissonClient() {
		String protocol = profile.equalsIgnoreCase("prod") ? "redis://" : "redis://";
		String address = protocol + redisHost + ":6379";
		
		Config config = new Config();
		config.useSingleServer().setAddress(address) // Replace with your Redis server address
				.setPassword(null) // Add password if Redis is secured
				.setConnectionPoolSize(10) //tối đa 10 kết nối Redis có thể được sử dụng cùng lúc.
				.setConnectionMinimumIdleSize(5);
		config.setLockWatchdogTimeout(30 * 1000); //30 secs 		
		return Redisson.create(config);
	}
}
