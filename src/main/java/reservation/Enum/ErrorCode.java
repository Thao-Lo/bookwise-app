package reservation.Enum;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	//User
	USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED),	 
	USER_NOT_FOUND(HttpStatus.NOT_FOUND),
	OAUTH2_INVALID_EMAIL(HttpStatus.CONFLICT),
	OAUTH2_SESSIONID_NOT_FOUND(HttpStatus.NOT_FOUND),
	
	INVALID_INPUT(HttpStatus.BAD_REQUEST),
	INVALID_USERNAME_OR_EMAIL(HttpStatus.BAD_REQUEST),
		
	INVALID_EMAIL(HttpStatus.BAD_REQUEST),
	UNVERIFIED_EMAIL(HttpStatus.BAD_REQUEST),
	VERIFIED_EMAIL(HttpStatus.CONFLICT),
	EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
	
	INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST),
	VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST),
	
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST),
	PASSWORD_MIXMATCH(HttpStatus.BAD_REQUEST),
	USERNAME_EXIST(HttpStatus.CONFLICT),
	EMAIL_EXIST(HttpStatus.CONFLICT),
	
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
	
	//Seat
	SEAT_NOT_FOUND(HttpStatus.NOT_FOUND), 
	SLOT_NOT_FOUND(HttpStatus.NOT_FOUND),
	SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND),
	
	//Reservation
	RESERVATION_FAILED(HttpStatus.CONFLICT),
	RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST),
	SLOT_UNAVAILABLE(HttpStatus.CONFLICT), 

	
	//Redis
	REDIS_SESSION_EXPIRED(HttpStatus.BAD_REQUEST),
	REDIS_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND),
	INVALID_RESERVATION_STATE(HttpStatus.BAD_REQUEST),
	REDIS_CLEAN_UP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
	//Payment
	PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_PAYMENT(HttpStatus.BAD_REQUEST), 
	PAYMENT_INCOMPLETE(HttpStatus.BAD_REQUEST),

	//Admin
	INVALID_ROLE(HttpStatus.BAD_REQUEST),
	INVALID_RESERVATION_STATUS(HttpStatus.BAD_REQUEST),
	
	//General
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR); 	
	
	private final HttpStatus status;

	ErrorCode(HttpStatus status) {
		this.status = status;
	}
}
