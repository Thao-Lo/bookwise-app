package reservation.Service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import reservation.Entity.GuestReservation;
import reservation.Entity.Schedule;
import reservation.Entity.Slot;
import reservation.Entity.Slot.Status;
import reservation.Entity.User;
import reservation.Repository.GuestReservationRepository;
import reservation.Repository.SlotRepository;
import reservation.Repository.UserRepository;

@Service
public class GuestReservationService {
	@Autowired
	GuestReservationRepository guestReservationRepository;

	@Autowired
	SlotRepository slotRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private RedissonClient redissonClient;

	public Page<GuestReservation> getAllReservation(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return guestReservationRepository.findAll(pageable);
	}

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

	@Transactional // modify multiple entries slot and reservation
	public void saveNewReservation(String username, Long slotId, Integer capacity) {
		if (username == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}
		if (slotId == null) {
			throw new IllegalArgumentException("Slot ID cannot be null");
		}
		if (capacity == null) {
			throw new IllegalArgumentException("Capacity cannot be null");
		}
		User user = userRepository.findByUsername(username);
		Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
		if (slot.getStatus() == Status.UNAVAILABLE) {
			throw new IllegalArgumentException("Slot is no longer available");
		}
		// ALTER TABLE guest_reservation ADD CONSTRAINT unique_user_slot UNIQUE
		// (user_id, slot_id);

		GuestReservation reservation = new GuestReservation();
		reservation.setUser(user);
		reservation.setSlot(slot);
		reservation.setNumberOfGuests(capacity);
		reservation.setStatus(GuestReservation.Status.BOOKED);
		guestReservationRepository.save(reservation);

		slot.setStatus(Status.UNAVAILABLE);
		slotRepository.save(slot);

	}

	public GuestReservation findReservationById(Long id) {
		return guestReservationRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
	}

	public boolean isStatusValid(String status) {
		for (GuestReservation.Status s : GuestReservation.Status.values()) {
			if (s.name().equalsIgnoreCase(status)) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public void updateReservationStatus(GuestReservation reservation) {
		guestReservationRepository.save(reservation);
		Slot slot = slotRepository.findById(reservation.getSlot().getId())
				.orElseThrow(() -> new IllegalArgumentException("Slot not found for reservation ID: " + reservation.getId()));
		;
		if (reservation.getStatus() == GuestReservation.Status.CANCELLED) {			
			slot.setStatus(Status.AVAILABLE);
			
		}else if(reservation.getStatus() == GuestReservation.Status.BOOKED) {
			slot.setStatus(Status.UNAVAILABLE);
		}
		slotRepository.save(slot);
	}
}
