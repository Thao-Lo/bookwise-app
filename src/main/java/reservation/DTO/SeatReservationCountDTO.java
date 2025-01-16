package reservation.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatReservationCountDTO {
	// seat id	
	public int seatId;
	public String seatName;
	public int reservationCount;

}
