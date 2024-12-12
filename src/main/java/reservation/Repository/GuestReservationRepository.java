package reservation.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import reservation.Entity.GuestReservation;


public interface GuestReservationRepository extends JpaRepository<GuestReservation, Long> {

}
	