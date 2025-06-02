package reservation.Exception;

import reservation.Enum.ErrorCode;

public class InvalidInputException extends BaseException {
	private static final long serialVersionUID = 1L;

	public InvalidInputException(ErrorCode code, String message) {
		super(code, message);
	}
}
