package reservation.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {
	Page<Slot> findAll(Pageable pageable);
	@Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.seat = :seat AND s.schedule= :schedule")
	boolean existsBySeatAndSchedule(@Param("seat") Seat seat, @Param("schedule") Schedule schedule);	
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Slot s WHERE s.schedule.datetime < :currentDateTime AND s.guestReservation IS NULL")
	// s.schedule and s.guestReservation is defined in Slot with these names
	void deleteByScheduleDatetimeBefore(@Param("currentDateTime") LocalDateTime currentDatetime);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacity(@Param("capacity") int capacity);
	
	@Query("SELECT s FROM Slot s WHERE s.schedule.datetime = :datetime AND s.status = 'AVAILABLE'")
	List<Slot> getSlotByDateAndTime(@Param("datetime") LocalDateTime datetime);
	
	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND s.schedule.datetime = :datetime AND s.status = 'AVAILABLE'")
	List<Slot> getSlotBySeatCapacityAndTime(@Param("capacity") int capacity, @Param("datetime") LocalDateTime datetime);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND FUNCTION('DATE', s.schedule.datetime) = :date AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacityAndDate(@Param("capacity") int capacity, @Param("date") LocalDate date);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND FUNCTION('DATE', s.schedule.datetime) = :date AND FUNCTION('TIME', s.schedule.datetime) = :time AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacityAndDateAndTime(@Param("capacity") int capacity, @Param("date") LocalDate date,
			@Param("time") LocalTime time);
	
	@Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.id = :id AND s.status = 'AVAILABLE'")
	boolean isSlotAvailable(@Param("id") long slotId);
	
	//if I have 50 days, but slot have 30 days, it will return 30 days
	@Query("SELECT s FROM Slot s WHERE s.schedule IN :schedules AND s.seat IN :seats")
	List<Slot> existingSlots(@Param("schedules") List<Schedule> schedules, @Param("seats") List<Seat> seats);	
}
