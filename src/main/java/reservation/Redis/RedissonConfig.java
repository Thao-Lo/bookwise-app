package reservation.Redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	// distributed storage: lock, cache,.

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379") // Replace with your Redis server address
				.setPassword(null) // Add password if Redis is secured
				.setConnectionPoolSize(10) //tối đa 10 kết nối Redis có thể được sử dụng cùng lúc.
				.setConnectionMinimumIdleSize(5);
		config.setLockWatchdogTimeout(30 * 1000); //30 secs 
		return Redisson.create(config);
	}
}
