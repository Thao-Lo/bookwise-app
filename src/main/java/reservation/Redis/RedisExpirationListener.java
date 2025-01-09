package reservation.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import reservation.Entity.Slot;
import reservation.Repository.SlotRepository;
import reservation.Service.SlotService;

@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
	@Autowired
	private SlotRepository slotRepository;
	
	@Autowired
	private SlotService slotService; 

	public RedisExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody());

		System.out.println("expired message: " + message);
		System.out.println("expiredKey" + expiredKey);

		if (expiredKey.startsWith("reservation:") && expiredKey.split(":").length == 3) {
			String[] keyItems = expiredKey.split(":");
			String slotIdString = keyItems[2];
			
			System.out.println("slotIdString" + slotIdString);
			
			if (slotIdString == null) {
				return;
			}
			Long slotId = Long.parseLong(slotIdString);
			slotService.markSlotHoldingToAvailable(slotId);
		}
	}

	private Long extractSlotIdFromKey(String key) {
		System.out.println("expired key" + key);
		return Long.parseLong(key.split(":")[1]);
	}

}
