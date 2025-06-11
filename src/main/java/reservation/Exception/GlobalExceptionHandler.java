package reservation.Exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import reservation.Controller.GuestReservationController;
import reservation.DTO.ErrorResponse;
import reservation.Enum.ErrorCode;


@ControllerAdvice
public class GlobalExceptionHandler {
	private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
		HttpStatus status = ex.getErrorCode().getStatus();
				
		ErrorResponse response = new ErrorResponse(
				status.value(),
				ex.getErrorCode(),
				ex.getMessage(),				
				request.getRequestURI()
				);
		
		return new ResponseEntity<>(response, status); //<T>: body, and statusCode
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		logger.error("Unhandle exception occured: ", ex);
		ErrorResponse response = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ErrorCode.INTERNAL_ERROR,
				"An unexpected error occured.",				
				request.getRequestURI()
				);		
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
		// Key: Field name (e.g., username).
		// Value: Error message (e.g., "Username is required.").
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors() //get a list of all validation errors
		.forEach(error -> 
		errors.put(error.getField(), error.getDefaultMessage())
		);
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
