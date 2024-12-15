package reservation.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import reservation.Entity.GuestReservation;


public interface GuestReservationRepository extends JpaRepository<GuestReservation, Long> {
	Page<GuestReservation> findAll(Pageable pageable);
	
	@Query("SELECT r FROM GuestReservation r WHERE r.slot.id = :slotId")
	GuestReservation findBySlotId(@Param("slotId") Long slotId);

}
	