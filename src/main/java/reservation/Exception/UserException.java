package reservation.Exception;

import reservation.Enum.ErrorCode;

public class UserException extends BaseException{
	private static final long serialVersionUID = 1L;

	public UserException(ErrorCode code, String message) {
		super(code, message);
	}
}
