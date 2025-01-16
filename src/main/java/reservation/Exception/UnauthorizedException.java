package reservation.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// return status 401 if Spring throw this exception
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
	//unique UID
	private static final long serialVersionUID = 1L;

	// constructor recieve message and send to super class
	public UnauthorizedException(String message) {
		// call constructor of super class
		super(message);
	}
}
