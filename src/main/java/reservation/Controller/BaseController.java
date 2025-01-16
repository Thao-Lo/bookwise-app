package reservation.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import reservation.Entity.Slot;
import reservation.Exception.NotFoundException;
import reservation.Exception.UnauthorizedException;
import reservation.Service.EmailService;
import reservation.Service.GuestReservationService;
import reservation.Service.JwtService;
import reservation.Service.RedisService;
import reservation.Service.ScheduleService;
import reservation.Service.SeatService;
import reservation.Service.SlotService;
import reservation.Service.StripeService;
import reservation.Service.UserService;
import reservation.Utils.JwtUtil;
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
			throw new UnauthorizedException(errorMessage);
		}
	}
	
	//if (slotsPage.isEmpty()) return new ResponseEntity<>(Map.of("Error", "No Slots found"), HttpStatus.NOT_FOUND);}
	protected void checkPageNotEmpty(Page<?> page, String errorMessage) {
		if(page.isEmpty() || page == null) {
			throw new NotFoundException(errorMessage);
		}
	}
	
	protected void checkListNotEmpty(List<?> list, String errorMessage) {
		if(list.isEmpty() || list == null) {
			throw new NotFoundException(errorMessage);
		}
	}	
	
	protected void checkIsSlotAvailable(boolean isSlotAvailable, String errorMessage) {
		if (!isSlotAvailable) {
			throw new NotFoundException(errorMessage);			
		}
	}
}
