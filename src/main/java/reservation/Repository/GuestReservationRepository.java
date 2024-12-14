package reservation.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import reservation.Entity.GuestReservation;


public interface GuestReservationRepository extends JpaRepository<GuestReservation, Long> {
	Page<GuestReservation> findAll(Pageable pageable);
}
	