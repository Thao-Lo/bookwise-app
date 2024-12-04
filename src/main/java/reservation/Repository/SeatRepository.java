package reservation.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import reservation.Entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

}
