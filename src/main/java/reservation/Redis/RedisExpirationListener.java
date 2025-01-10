package reservation.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;

import reservation.Entity.Slot;
import reservation.Repository.SlotRepository;
import reservation.Service.SlotService;
import reservation.Service.StripeService;

@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
	@Autowired
	private SlotRepository slotRepository;
	@Autowired
	private SlotService slotService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private StripeService stripeService;

	public RedisExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String expiredKey = new String(message.getBody());
			System.out.println("expired message: " + message);
			System.out.println("expiredKey" + expiredKey);

			if (expiredKey.startsWith("reservation:") && expiredKey.split(":").length == 2) {
				String[] keyItems = expiredKey.split(":");
				String sessionId = keyItems[1];

				String backupKey = "backup:" + sessionId;

				String slotIdString = (String) redisTemplate.opsForHash().get(backupKey, "slotId");
				
				String paymentIntentId = (String) redisTemplate.opsForHash().get(backupKey, "paymentIntentId");
				if (slotIdString != null) {
					System.out.println("slotIdString" + slotIdString);
					Long slotId = Long.parseLong(slotIdString);
					slotService.markSlotHoldingToAvailable(slotId);
				}
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
