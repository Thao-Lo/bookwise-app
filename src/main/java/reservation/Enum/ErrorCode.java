package reservation.Enum;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND), 
	USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
	INVALID_INPUT(HttpStatus.BAD_REQUEST), 
	RESERVATION_FAILED(HttpStatus.CONFLICT),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR), 
	SESSION_EXPIRED(HttpStatus.BAD_REQUEST),
	SLOT_UNAVAILABLE(HttpStatus.CONFLICT), 
	PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_PAYMENT(HttpStatus.BAD_REQUEST), 
	PAYMENT_INCOMPLETE(HttpStatus.BAD_REQUEST);

	private final HttpStatus status;

	ErrorCode(HttpStatus status) {
		this.status = status;
	}
}
