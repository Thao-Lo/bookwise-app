package reservation.Exception;

import reservation.Enum.ErrorCode;

public class SlotUnavailableException extends BaseException{
	private static final long serialVersionUID = 1L;

	public SlotUnavailableException(String message) {
		super(message, ErrorCode.SLOT_UNAVAILABLE);
	}
}
