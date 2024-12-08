package reservation.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import reservation.Entity.Schedule;
import reservation.Entity.Seat;
import reservation.Entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {
	@Query("SELECT COUNT(s) > 0 FROM Slot s WHERE s.seat = :seat AND s.schedule= :schedule")
	boolean existsBySeatAndSchedule(@Param("seat") Seat seat, @Param("schedule") Schedule schedule);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Slot s WHERE s.schedule.datetime < :currentDateTime AND s.guestReservation IS NULL")
	// s.schedule and s.guestReservation is defined in Slot with these names
	void deleteByScheduleDatetimeBefore(@Param("currentDateTime") LocalDateTime currentDatetime);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacity(@Param("capacity") int capacity);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND FUNCTION('TIME', s.schedule.datetime) = :time AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacityAndTime(@Param("capacity") int capacity, @Param("time") LocalTime time);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND FUNCTION('DATE', s.schedule.datetime) = :date AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacityAndDate(@Param("capacity") int capacity, @Param("date") LocalDate date);

	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND FUNCTION('DATE', s.schedule.datetime) = :date AND FUNCTION('TIME', s.schedule.datetime) = :time AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacityAndDateAndTime(@Param("capacity") int capacity, @Param("date") LocalDate date,
			@Param("time") LocalTime time);
}
