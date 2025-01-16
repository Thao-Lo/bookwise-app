package reservation.Controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reservation.Entity.Slot;
import reservation.Exception.NotFoundException;
import reservation.Exception.UnauthorizedException;

public abstract class BaseController {

	//throw exception
	protected void checkPrincipal(Principal principal, String errorMessage) {
		if(principal == null) {
			throw new UnauthorizedException(errorMessage);
		}
	}
	
	//if (slotsPage.isEmpty()) return new ResponseEntity<>(Map.of("Error", "No Slots found"), HttpStatus.NOT_FOUND);}
	protected void checkPageNotEmpty(Page<?> page, String errorMessage) {
		if(page.isEmpty() || page == null) {
			throw new NotFoundException(errorMessage);
		}
	}
}
