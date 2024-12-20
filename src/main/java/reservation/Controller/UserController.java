package reservation.Controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import reservation.Service.EmailService;
import reservation.Service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	EmailService emailService;
	@Autowired
	PasswordEncoder passwordEncoder;
	

	@PostMapping("/register")
	public ResponseEntity<Object> registerNewUser(@Valid @RequestBody RegisterRequest request) {

		if (userService.isUsernameExist(request.getUsername())) {
			return new ResponseEntity<>(Map.of("error","Username is already existed."), HttpStatus.CONFLICT);
		}
		if (userService.isEmailExist(request.getEmail())) {
			return new ResponseEntity<>(Map.of("error","Email is already existed."), HttpStatus.CONFLICT);
		}
		// confirm re-enter password
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			return new ResponseEntity<>(Map.of("error","Passwords are not matched"), HttpStatus.BAD_REQUEST);
		}
		System.out.println("register password: " + request.getPassword());
		System.out.println("Hashed password (register): " + passwordEncoder.encode(request.getPassword()));
		
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

		emailService.sendVerificationEmail(request.getEmail(), verificationCode);

		return new ResponseEntity<>(Map.of("message","User Register successfully. Please check your email to verify your account."), HttpStatus.CREATED);
	}

	@PostMapping("/verify-email")
	public ResponseEntity<Object> verifyEmail(@RequestParam @NotEmpty @Email String email, @RequestParam @NotEmpty String code) {
		User user = userService.findUserByEmail(email);

		if (user == null) {
			return new ResponseEntity<>(Map.of("error","User is not found."), HttpStatus.NOT_FOUND);
		}
		if (!user.getVerificationCode().equals(code)) {
			return new ResponseEntity<>(Map.of("error","Invalid verification code."), HttpStatus.BAD_REQUEST);
		}
		if (user.getCodeExpirationTime() == null || user.getCodeExpirationTime().isBefore(LocalDateTime.now())) {
			return new ResponseEntity<>(Map.of("error","verification code expired"), HttpStatus.BAD_REQUEST);
		}
		user.setEmailVerified(true);
		user.setVerificationCode(null);
		user.setCodeExpirationTime(null);
		userService.saveUser(user);
		return new ResponseEntity<>(Map.of("message","Email verified successfully"), HttpStatus.OK);
	}

	@PostMapping("/resend-verification-code")
	public ResponseEntity<String> resendVerificationCode(@RequestParam @NotEmpty @Email String email) {
		User user = userService.findUserByEmail(email);

		if (user == null) {
			return new ResponseEntity<>("User is not found.", HttpStatus.NOT_FOUND);
		}
		if(user.isEmailVerified()) {
			return new ResponseEntity<>("Email is already verified.", HttpStatus.BAD_REQUEST);
		}
		String newCode = UUID.randomUUID().toString();

		user.setVerificationCode(newCode);		
		user.setCodeExpirationTime(LocalDateTime.now().plusHours(1));
		
		userService.saveUser(user);
		emailService.sendVerificationEmail(user.getEmail(), newCode);
		return new ResponseEntity<>("Verification code resent successfully.", HttpStatus.OK);
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