package reservation.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import reservation.Entity.GuestReservation;
import reservation.Entity.Slot;

public interface GuestReservationRepository extends JpaRepository<GuestReservation, Long> {

}
