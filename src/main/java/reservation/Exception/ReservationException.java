package reservation.Exception;

import reservation.Enum.ErrorCode;

public class ReservationException extends BaseException{
	private static final long serialVersionUID = 1L;

	public ReservationException(ErrorCode code, String message) {
		super(code, message);
	}

}
