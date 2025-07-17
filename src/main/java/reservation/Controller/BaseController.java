package reservation.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;

import reservation.Enum.ErrorCode;
import reservation.Exception.NotFoundException;
import reservation.Exception.UnauthorizedException;
import reservation.Service.EmailService;
import reservation.Service.GuestReservationService;
import reservation.Service.RedisService;
import reservation.Service.SlotService;
import reservation.Service.UserService;
import reservation.Utils.TimeZoneConverter;

public abstract class BaseController {
	@Autowired
	protected RedisService redisService;
	@Autowired
	protected GuestReservationService guestReservationService;
	@Autowired
	protected EmailService emailService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;
	@Autowired
	protected TimeZoneConverter timeZoneConverter;
	@Autowired
	protected SlotService slotService;
	

	//throw exception
	protected void checkPrincipal(Principal principal, String errorMessage) {
		if(principal == null) {
			throw new UnauthorizedException(ErrorCode.USER_UNAUTHORIZED, errorMessage);
		}
	}
	
	//if (slotsPage.isEmpty()) return new ResponseEntity<>(Map.of("Error", "No Slots found"), HttpStatus.NOT_FOUND);}
	protected void checkPageNotEmpty(Page<?> page, ErrorCode code, String errorMessage) {
		if(page.isEmpty() || page == null) {
			throw new NotFoundException(code, errorMessage);
		}
	}
	
	protected void checkListNotEmpty(List<?> list, ErrorCode code, String errorMessage) {
		if(list.isEmpty() || list == null) {
			throw new NotFoundException(code, errorMessage);
		}
	}	
	
	protected void checkIsSlotAvailable(boolean isSlotAvailable, ErrorCode code, String errorMessage) {
		if (!isSlotAvailable) {
			throw new NotFoundException(code, errorMessage);			
		}
	}
}
