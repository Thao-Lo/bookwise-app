package reservation.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import reservation.Entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

	Schedule findByDatetime(LocalDateTime datetime);

	//Must be name of Entity, not table name in db or else use nativeQuery
	@Query("SELECT s.datetime FROM Schedule s where s.datetime BETWEEN :startDate AND :endDate")
	List<LocalDateTime> findAllDatetimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);
	
	@Transactional
	@Modifying 
	@Query("DELETE FROM Schedule s WHERE s.datetime < :currentDateTime")	
	void deleteByDatetimeBefore(@Param("currentDateTime") LocalDateTime currentDatetime);
}
