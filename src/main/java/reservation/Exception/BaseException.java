package reservation.Exception;


import lombok.Getter;
import reservation.Enum.ErrorCode;
 
@Getter
public class BaseException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;
	
	public BaseException(ErrorCode errorCode, String message) {		
		super(message);
		this.errorCode = errorCode;
	}
	
}
