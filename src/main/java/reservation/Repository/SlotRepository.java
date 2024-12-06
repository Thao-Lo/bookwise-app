package reservation.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import reservation.Entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>{

	@Transactional
	@Modifying 
	@Query("DELETE FROM Slot s WHERE s.schedule.datetime < :currentDateTime AND s.guestReservation IS NULL")
	//s.schedule and s.guestReservation is defined in Slot with these names
	void deleteByScheduleDatetimeBefore(@Param("currentDateTime") LocalDateTime currentDatetime);
	
	@Query("SELECT s FROM Slot s WHERE s.seat.capacity = :capacity AND s.status = 'AVAILABLE'")
	List<Slot> getSlotsBySeatCapacity(@Param("capacity") int capacity);
	
}
