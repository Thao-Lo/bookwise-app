package reservation.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {
	private long id;
	private String tableName;
	private int capacity;
	private LocalDate date;
	private LocalTime time;
	private String status;
	
	public SlotResponse(long id, String tableName, int capacity, LocalDate date, LocalTime time) {
		super();
		this.id = id;
		this.tableName = tableName;
		this.capacity = capacity;
		this.date = date;
		this.time = time;
	}
	
}
