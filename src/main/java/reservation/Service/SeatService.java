package reservation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reservation.Entity.Seat;
import reservation.Repository.SeatRepository;

@Service
public class SeatService {

	@Autowired
	SeatRepository seatRepository;
	
	public Page<Seat> getAllSeat(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return seatRepository.findAll(pageable);
	}
}
