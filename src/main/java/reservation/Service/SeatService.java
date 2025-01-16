package reservation.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reservation.DTO.SeatReservationCountDTO;
import reservation.Entity.Seat;
import reservation.Repository.SeatRepository;

@Service
public class SeatService {

	@Autowired
	SeatRepository seatRepository;

	public Page<Seat> getAllSeat(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return seatRepository.findAll(pageable);
	}

	public List<SeatReservationCountDTO> countTotalReservationsPerSeat(){
		List<Object[]> results = seatRepository.countTotalReservationsPerSeat();
		List<SeatReservationCountDTO> list = new ArrayList<>();
		
		for(Object[] row : results){			
			SeatReservationCountDTO dto =  new SeatReservationCountDTO
					(		
					((Number) row[0]).intValue(),
					(String) row[1],
					((Number) row[2]).intValue()
					);
					list.add(dto);
		}		
		return list;
	}
}
