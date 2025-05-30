package reservation.Exception;

import reservation.Enum.ErrorCode;

public class SessionExpiredException extends BaseException{
	private static final long serialVersionUID = 1L;

	public SessionExpiredException(String message) {
		super(message, ErrorCode.SESSION_EXPIRED);
	}
}
