package reservation.Exception;

import reservation.Enum.ErrorCode;

public class PaymentException extends BaseException{
	private static final long serialVersionUID = 1L;

	public PaymentException(ErrorCode code, String message) {
		super(code, message);
	}
	
}
