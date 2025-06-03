package reservation.Exception;

import reservation.Enum.ErrorCode;

public class AdminException extends BaseException{
	private static final long serialVersionUID = 1L;

	public AdminException(ErrorCode code, String message) {
		super(code, message);
	}
}
