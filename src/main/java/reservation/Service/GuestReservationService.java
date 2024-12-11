package reservation.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reservation.Repository.GuestReservationRepository;
import reservation.Repository.SlotRepository;

@Service
public class GuestReservationService {
	@Autowired
	GuestReservationRepository guestReservationRepository;

	@Autowired
	SlotRepository slotRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private RedissonClient redissonClient;

	public boolean isSlotReserve(String slotKey) {
		String lockKey = "lock:" + slotKey;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			// if (lock.tryLock(10, 30, TimeUnit.SECONDS)) expired after 30 sec
			if (lock.tryLock(10, TimeUnit.SECONDS)) {
				// LockWatchdog will automatically renew the lease time (default is 30 seconds)
				System.out.println("Slot reserved for: " + slotKey);
				return true;

			} else {
				System.out.println("Failed to acquire lock for slot " + slotKey);
				return false;
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Thread interrupted while waiting for lock: " + lockKey);
			return false;
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	public boolean isSlotAvailable(Long slotId) {
		return slotRepository.isSlotAvailable(slotId);
	}
}
