package reservation.Controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import reservation.DTO.RegisterRequest;
import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Enum.ErrorCode;
import reservation.Exception.UserException;
import reservation.Service.EmailService;
import reservation.Service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	//SLF4J
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	@Autowired
	EmailService emailService;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public ResponseEntity<Object> getHomePage(){	
		return new ResponseEntity<>(Map.of("message", "Welcome to Zavis booking management system"),HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<Object> registerNewUser(@Valid @RequestBody RegisterRequest request) {
		
		if (userService.isUsernameExist(request.getUsername())) {
			throw new UserException(ErrorCode.USERNAME_EXIST, String.format("Username: %s is already existed.", request.getUsername()));
		}
		if (userService.isEmailExist(request.getEmail())) {
			throw new UserException(ErrorCode.EMAIL_EXIST, String.format("Email: %s is already existed.", request.getEmail()));
		}
		// confirm re-enter password
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new UserException(ErrorCode.PASSWORD_MIXMATCH, "Passwords are not matched");
		}
		logger.info("Register: valid inputs received for username: {}", request.getUsername());		
		
		String verificationCode = userService.generateVerificationCode();
		
		// save to db
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.valueOf(request.getRole() != null ? request.getRole() : "GUEST"));
		user.setVerificationCode(verificationCode);
		user.setEmailVerified(false);
		user.setCodeExpirationTime(LocalDateTime.now().plusHours(1));
		userService.saveUser(user);
		logger.debug("Register: store new user details to database for user: {}", request.getUsername());
		
		emailService.sendVerificationEmail(request.getEmail(), verificationCode);
		logger.info("Register: send UUID code to: {}", request.getEmail());
		
		return new ResponseEntity<>(Map.of("message","User Register successfully. Please check your email to verify your account."), HttpStatus.CREATED);
	}

	@PostMapping("/verify-email")
	public ResponseEntity<Object> verifyEmail(@RequestParam @NotEmpty @Email String email, @RequestParam @NotEmpty String code) {
		User user = userService.findUserByEmail(email);

		if (user == null) {
			throw new UserException(ErrorCode.USER_NOT_FOUND, String.format("User is not found for email: %s", email));
		}
		if (!user.getVerificationCode().equals(code)) {
			throw new UserException(ErrorCode.INVALID_VERIFICATION_CODE, "Invalid verification code.");
		}
		if (user.getCodeExpirationTime() == null || user.getCodeExpirationTime().isBefore(LocalDateTime.now())) {
			throw new UserException(ErrorCode.VERIFICATION_CODE_EXPIRED, "Verification code expired");
		}
		user.setEmailVerified(true);
		user.setVerificationCode(null);
		user.setCodeExpirationTime(null);
		userService.saveUser(user);
		logger.info("Verify Email: successfully verified email for user: {}", user.getUsername());
		return new ResponseEntity<>(Map.of("message","Email verified successfully"), HttpStatus.OK);
	}

	@PostMapping("/resend-verification-code")
	public ResponseEntity<Object> resendVerificationCode(@RequestParam @NotEmpty @Email String email) {
		User user = userService.findUserByEmail(email);

		if (user == null) {
			throw new UserException(ErrorCode.USER_NOT_FOUND, ("User is not found for email: " + email));
		}
		if(user.isEmailVerified()) {
			throw new UserException(ErrorCode.VERIFIED_EMAIL, "Email is already verified.");
		}
		String newCode = UUID.randomUUID().toString();

		user.setVerificationCode(newCode);		
		user.setCodeExpirationTime(LocalDateTime.now().plusHours(1));
		
		userService.saveUser(user);
		emailService.sendVerificationEmail(user.getEmail(), newCode);
		return new ResponseEntity<>(Map.of("message","Verification code resent successfully."), HttpStatus.OK);
	}

}
// confirm email
//if (!userService.isValidEmailPattern(email)) {
//	throw new IllegalArgumentException("Incorrect email pattern");
//}
//if (username.isEmpty() || username == null || email.isEmpty() || email == null || password.isEmpty()
//|| password == null || confirmPassword.isEmpty() || confirmPassword == null) {
//throw new IllegalArgumentException("Invalid Input");
//}
// Username
// 3 < length < 15
//if (!userService.isValidUsernameLength(username)) {
//throw new IllegalArgumentException("Username must be between 3 and 15 characters long.");
//}
// valid?
// confirm password pattern
//if (!userService.isValidPasswordPattern(password)) {
//	throw new IllegalArgumentException("Incorrect password pattern");
//}