package reservation.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import reservation.DTO.ErrorResponse;
import reservation.Enum.ErrorCode;


@ControllerAdvice
public class GlobalExceptionHandler {
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
		ErrorResponse response = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ErrorCode.INTERNAL_ERROR,
				ex.getMessage(),				
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
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex){
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	// Catch exception
	// Customize Exception to check principal, return status 401
	@ExceptionHandler (UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex ){
		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}
	
//	@ExceptionHandler (NotFoundException.class)
//	public ResponseEntity<Object> handleNotFoundException(NotFoundException ex){
//		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND); 
//	}	

}
