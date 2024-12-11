package reservation.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
//	private String sessionId;
	private long id;
	private String tableName;
	private int capacity;
	private LocalDate date;
	private LocalTime time;		
}
