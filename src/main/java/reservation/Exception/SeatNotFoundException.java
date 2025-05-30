package reservation.Exception;

import reservation.Enum.ErrorCode;

public class SeatNotFoundException extends BaseException{

	private static final long serialVersionUID = 1L;

	public SeatNotFoundException(String message) {
		super(message, ErrorCode.SEAT_NOT_FOUND);
	}
}
