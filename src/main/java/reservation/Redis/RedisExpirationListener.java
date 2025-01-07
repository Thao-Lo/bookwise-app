package reservation.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import reservation.Entity.Slot;
import reservation.Repository.SlotRepository;

@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
	@Autowired
	private SlotRepository slotRepository;

	public RedisExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		if (expiredKey.startsWith("reservation:")) {
			Long slotId = extractSlotIdFromKey(expiredKey);

			Slot slot = slotRepository.findById(slotId).orElse(null);
			if (slot != null && slot.getStatus() == Slot.Status.UNAVAILABLE) {
				slot.setStatus(Slot.Status.AVAILABLE);
				slotRepository.save(slot);
			}
		}
	}

	
	private Long extractSlotIdFromKey(String key) {
		return Long.parseLong(key.split(":")[1]);
	}

}
