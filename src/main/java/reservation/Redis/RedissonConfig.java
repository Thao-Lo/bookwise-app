package reservation.Redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	//distributed storage: lock, cache,.
	
	@Bean	
	public RedissonClient redissonClient() {
//		 Config config = new Config();
//		    config.useSingleServer()
//		          .setAddress("redis://127.0.0.1:6379")
//		          .setDatabase(0); // Sử dụng database mặc định là 0
		    return Redisson.create();
	}
}
