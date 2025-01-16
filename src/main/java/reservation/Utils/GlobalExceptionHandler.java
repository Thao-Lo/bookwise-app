package reservation.Utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import reservation.Exception.UnauthorizedException;

@ControllerAdvice
public class GlobalExceptionHandler {
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
	
//	 @ExceptionHandler(UserNotFoundException.class)
//	public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex){
//		return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
//	}
	 
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<String> handleGenericException(Exception ex) {
//	    return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
//	}
}
