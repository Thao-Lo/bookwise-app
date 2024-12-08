package reservation.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {
	private String tableName;
	private int capacity;
	private LocalDate date;
	private LocalTime time;
//	private String status;
}
