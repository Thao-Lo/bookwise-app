package reservation.Redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Primary
@Configuration
//@DependsOn("startRedisServer")
public class RedisConfig {
	 // must have return method for bean
	@Bean
	@DependsOn({"customLettuceConnectionFactory"})
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();

		// gắn RedisConnectionFactory vào RedisTemplate, để RedisTemplate biết cách kết
		// nối app  port 8080 với Redis server port 6379.
		template.setConnectionFactory(redisConnectionFactory);

		// Chuyển đổi các khóa (key) trong Redis thành chuỗi (String).
		template.setKeySerializer(new StringRedisSerializer());
		// Chuyển đổi các giá trị (value) thành JSON.
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		// Cấu hình để các khóa trong Hash được lưu trữ dưới dạng chuỗi (String).
		template.setHashKeySerializer(new StringRedisSerializer());
		// Chuyển đổi các giá trị trong Hash thành JSON
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

		// Gọi phương thức này để kiểm tra và áp dụng các cấu hình đã đặt ở trên.
		template.afterPropertiesSet();

		return template;
	}
	
	@Bean
	@DependsOn({"customLettuceConnectionFactory"})
	public RedisMessageListenerContainer redisMessageListenerContainer(LettuceConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		return container;
	}	
	
}
