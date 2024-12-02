package reservation.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reservation.DTO.RegisterRequest;
import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> registerNewUser(@Valid @RequestBody RegisterRequest request) {

		if (userService.isUsernameExist(request.getUsername())) {
			return new ResponseEntity<>("Username is already existed.", HttpStatus.CONFLICT);
		}
		if (userService.isEmailExist(request.getEmail())) {
			return new ResponseEntity<>("Email is already existed.", HttpStatus.CONFLICT);
		}
		// confirm re-enter password
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			return new ResponseEntity<>("Passwords are not matched", HttpStatus.BAD_REQUEST);
		}
		// save to db
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole(Role.valueOf(request.getRole() != null ? request.getRole() : "GUEST"));
		userService.saveUser(user);
		return new ResponseEntity<>("User Register successfully", HttpStatus.CREATED);
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