package reservation.Exception;

import reservation.Enum.ErrorCode;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException{

	private static final long serialVersionUID = 1L;

	public NotFoundException(ErrorCode code, String message) {
		super(code, message);
	}
}
