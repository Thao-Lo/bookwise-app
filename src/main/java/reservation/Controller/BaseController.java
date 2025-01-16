package reservation.Controller;

import java.security.Principal;

import reservation.Exception.UnauthorizedException;

public abstract class BaseController {

	//throw exception
	protected void checkPrincipal(Principal principal, String errorMessage) {
		if(principal == null) {
			throw new UnauthorizedException(errorMessage);
		}
	}
	
}
