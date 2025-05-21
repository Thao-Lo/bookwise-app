package reservation.Redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class RedisListenerConfig {
    @Bean
    public RedisExpirationListener redisExpirationListener(RedisMessageListenerContainer container,
                                                           SlotService slotService,
                                                           RedisService redisService,
                                                           StripeService stripeService) {
        return new RedisExpirationListener(container, slotService, redisService, stripeService);
    }
}
