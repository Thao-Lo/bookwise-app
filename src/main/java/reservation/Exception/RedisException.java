package reservation.Exception;

import reservation.Enum.ErrorCode;

public class RedisException extends BaseException{
	private static final long serialVersionUID = 1L;

	public RedisException(ErrorCode code, String message) {
		super(code, message);
	}
}
