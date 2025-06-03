package reservation.Service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import reservation.Entity.GuestReservation;
import reservation.Entity.Slot;
import reservation.Entity.Slot.Status;
import reservation.Entity.User;
import reservation.Enum.ErrorCode;
import reservation.Exception.InvalidInputException;
import reservation.Exception.ReservationException;
import reservation.Exception.SlotException;
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
	private final static Logger logger = LoggerFactory.getLogger(GuestReservationService.class);

	public Page<GuestReservation> getAllReservation(Pageable pageable) {
//		Pageable pageable = PageRequest.of(page, size);
		return guestReservationRepository.findAll(pageable);
	}

	public Page<GuestReservation> getReservationByUserId(int page, int size, Long userId) {
		Pageable pageable = PageRequest.of(page, size);
		return guestReservationRepository.findByUserId(pageable, userId);
	}

	public boolean isSlotReserve(Long slotId) {
		String slotKey = "slot:" + slotId;
		String lockKey = "lock:" + slotKey;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			// if (lock.tryLock(10, 30, TimeUnit.SECONDS)) expired after 30 sec
			if (lock.tryLock(10, TimeUnit.SECONDS)) {
				// LockWatchdog will automatically renew the lease time (default is 30 seconds)
				logger.info("Reddison: Slot reserved for: {}", slotKey);			
				return true;

			} else {
				logger.warn("Reddison: Failed to acquire lock for slot: {}", slotKey);					
				return false;
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("Reddison: Thread interrupted while waiting for lock: ", lockKey);			
			return false;
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
	
	public void releaseRedissonLock(Long slotId) {
		String lockKey = "lock:" + "slot:" + slotId;
		RLock lock = redissonClient.getLock(lockKey);
		if (lock.isLocked() && lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}	

	public boolean isSlotAvailable(Long slotId) {
		return slotRepository.isSlotAvailable(slotId);
	}

	@Transactional // modify multiple entries slot and reservation
	public void saveNewReservation(String username, Long slotId, Integer capacity) {
		if (username == null) {
			throw new InvalidInputException(ErrorCode.INVALID_INPUT, "Username cannot be null");
		}
		if (slotId == null) {
			throw new InvalidInputException(ErrorCode.INVALID_INPUT, "Slot ID cannot be null");
		}
		if (capacity == null) {
			throw new InvalidInputException(ErrorCode.INVALID_INPUT, "Capacity cannot be null");
		}
		User user = userRepository.findByUsername(username);
		Slot slot = slotRepository.findById(slotId).orElseThrow(() -> new SlotException(ErrorCode.SLOT_NOT_FOUND, "Slot not found"));
		if (slot.getStatus() == Status.UNAVAILABLE) {
			throw new SlotException(ErrorCode.SLOT_UNAVAILABLE, "Slot is no longer available");
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
				.orElseThrow(() -> new ReservationException(ErrorCode.RESERVATION_NOT_FOUND, "Reservation not found for ID: " + id));

	}
	public GuestReservation findReservationBySlotId(Long id) {
		return guestReservationRepository.findBySlotId(id)
				.orElseThrow(() -> new ReservationException(ErrorCode.RESERVATION_NOT_FOUND, "Reservation not found for Slot Id: " + id));

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
		Slot slot = slotRepository.findById(reservation.getSlot().getId()).orElseThrow(
				() -> new SlotException(ErrorCode.SLOT_NOT_FOUND, "Slot not found for reservation ID: " + reservation.getId()));
		;
		if (reservation.getStatus() == GuestReservation.Status.CANCELLED) {
			slot.setStatus(Status.AVAILABLE);

		} else if (reservation.getStatus() == GuestReservation.Status.BOOKED) {
			slot.setStatus(Status.UNAVAILABLE);
		}
		slotRepository.save(slot);
	}

}
