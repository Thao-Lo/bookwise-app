package reservation.Exception;

import reservation.Enum.ErrorCode;

public class EmailException extends BaseException{

	private static final long serialVersionUID = 1L;

	public EmailException(ErrorCode code, String message) {
		super(code, message);
	}
}
