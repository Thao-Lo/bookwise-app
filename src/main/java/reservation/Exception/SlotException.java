package reservation.Exception;

import reservation.Enum.ErrorCode;

public class SlotException extends BaseException{
	private static final long serialVersionUID = 1L;

	public SlotException(ErrorCode code, String message) {
		super(code, message);
	}

}
