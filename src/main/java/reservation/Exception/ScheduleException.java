package reservation.Exception;

import reservation.Enum.ErrorCode;

public class ScheduleException extends BaseException{
	private static final long serialVersionUID = 1L;

	public ScheduleException(ErrorCode code, String message) {
		super(code, message);
	}

}
