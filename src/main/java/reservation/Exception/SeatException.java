package reservation.Exception;

import reservation.Enum.ErrorCode;

public class SeatException extends BaseException{
	private static final long serialVersionUID = 1L;

	public SeatException(ErrorCode code, String message) {
		super(code, message);
	}

}
