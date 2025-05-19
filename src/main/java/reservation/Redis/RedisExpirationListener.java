package reservation.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;

import reservation.Service.RedisService;
import reservation.Service.SlotService;
import reservation.Service.StripeService;

@Lazy
@Component
@DependsOn("redisMessageListenerContainer")
public class RedisExpirationListener extends KeyExpirationEventMessageListener {	
	@Autowired
	private SlotService slotService;	
	@Autowired 
	private RedisService redisService;	
	@Autowired
	private StripeService stripeService;

	public RedisExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
		System.out.println("RedisExpirationListener initialized");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String expiredKey = new String(message.getBody());
			System.out.println("expired message: " + message);
			System.out.println("expiredKey" + expiredKey);
			
			// get sessionId from expired Redis key
			if (expiredKey.startsWith("reservation:") && expiredKey.split(":").length == 2) {
				String[] keyItems = expiredKey.split(":");
				String sessionId = keyItems[1];
				
				// use sessionId to generate again the backup key
				String backupKey = redisService.generateRedisBackupKey(sessionId);
				
				// get slotId and paymentIntentID from the key, String type values 
				String slotIdString = redisService.getHashValue(backupKey, "slotId");				
				String paymentIntentId = redisService.getHashValue(backupKey, "paymentIntentId");
				
				// parse slotId from String to Long, then change slot status from Holding to Available
				if (slotIdString != null) {
					System.out.println("slotIdString" + slotIdString);
					Long slotId = Long.parseLong(slotIdString);
					slotService.markSlotHoldingToAvailable(slotId);
				}
				// timeout, so cancel the paymentIntent from Stripe
				if (paymentIntentId != null) {
					stripeService.cancelPaymentIntent(paymentIntentId, "duplicate");
				}
			}
		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Long extractSlotIdFromKey(String key) {
		System.out.println("expired key" + key);
		return Long.parseLong(key.split(":")[1]);
	}

}
