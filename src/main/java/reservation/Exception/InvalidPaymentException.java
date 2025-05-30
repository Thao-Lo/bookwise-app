package reservation.Exception;

import reservation.Enum.ErrorCode;

public class InvalidPaymentException extends BaseException{

	private static final long serialVersionUID = 1L;

	public InvalidPaymentException(String message) {
		super(message, ErrorCode.INVALID_PAYMENT);
	}

}
