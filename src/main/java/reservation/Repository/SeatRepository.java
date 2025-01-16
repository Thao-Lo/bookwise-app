package reservation.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import reservation.DTO.SeatReservationCountDTO;
import reservation.Entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
	Page<Seat> findAll(Pageable pageable);

	//space at the end of each LOC
	@Query("SELECT s.id as seatId, s.seatName as seatName, COUNT(g.id) as reservationCount FROM Seat s "
			+ "LEFT JOIN Slot sl ON s.id = sl.seat.id "
			+ "LEFT JOIN GuestReservation g ON sl.id = g.slot.id "
			+ "GROUP BY s.id, s.seatName "
			+ "ORDER BY s.id")
	List<Object[]> countTotalReservationsPerSeat();
}
