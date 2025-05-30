package reservation.Exception;

import reservation.Enum.ErrorCode;

public class PaymentIncompleteException extends BaseException{

	private static final long serialVersionUID = 1L;

	public PaymentIncompleteException(String message) {
		super(message, ErrorCode.PAYMENT_INCOMPLETE);
	}

}
