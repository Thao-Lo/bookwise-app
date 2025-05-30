package reservation.DTO;

import java.time.LocalDateTime;

import lombok.Data;
import reservation.Enum.ErrorCode;

@Data
public class ErrorResponse {
	private LocalDateTime timestamp;
	private int status;
	private ErrorCode errorCode;
	private String message;	
	private String path;
	
	public ErrorResponse(int status, ErrorCode errorCode, String message, String path) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;		
		this.path = path;
	}
	
}
