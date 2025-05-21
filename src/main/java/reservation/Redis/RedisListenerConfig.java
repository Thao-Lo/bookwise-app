//package reservation.Redis;
//
//import reservation.Service.StripeService;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//
//import reservation.Service.RedisService;
//import reservation.Service.SlotService;
//
//
//@Configuration
////@Profile("prod")
//public class RedisListenerConfig {
//    @Bean
//    public RedisExpirationListener redisExpirationListener(RedisMessageListenerContainer container,
//                                                           SlotService slotService,
//                                                           RedisService redisService,
//                                                           StripeService stripeService) {
//        return new RedisExpirationListener(container, slotService, redisService, stripeService);
//    }
//}
