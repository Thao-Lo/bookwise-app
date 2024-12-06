package reservation.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {
	private String tableName;
	private int capacity;
	private LocalDateTime datetime;
//	private String status;
}
