package reservation.Exception;

import reservation.Enum.ErrorCode;

// return status 401 if Spring throw this exception
//@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {
	//unique UID
	private static final long serialVersionUID = 1L;

	// constructor recieve message and send to super class
	public UnauthorizedException(ErrorCode code, String message) {
		// call constructor of super class
		super(code, message);
	}
}
