package reservation.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Service.UserService;

@RestController
@RequestMapping("api/v1/admin/user")
public class AdminUserController {

	@Autowired
	UserService userService;

	@GetMapping("/list")
	public ResponseEntity<Object> getUserList(Principal principal) {

		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "Not authorized."), HttpStatus.UNAUTHORIZED);
		}
		List<User> users = userService.showAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity<>(Map.of("error", "No users found."), HttpStatus.NOT_FOUND);
		}
		List<UserResponse> userList = new ArrayList<>();
		for(User user: users) {
			UserResponse userResponse = new UserResponse();
			userResponse.setId(user.getId());
			userResponse.setUsername(user.getUsername());
			userResponse.setEmail(user.getEmail());
			userResponse.setRole(user.getRole().name());
			userList.add(userResponse);
		}
					
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}
	
	@PostMapping("/edit-role/{id}/{role}")
	public ResponseEntity<Object> editUserRole(Principal principal, @PathVariable Long id, @PathVariable String role){
		if (principal == null) {
			return new ResponseEntity<>(Map.of("error", "Not authorized."), HttpStatus.UNAUTHORIZED);
		}
		if(!userService.isValidRole(role)) {
			return new ResponseEntity<>(Map.of("error", "Invalid role provided."), HttpStatus.BAD_REQUEST);
		}
		User user = userService.findUserById(id);		
		user.setRole(Role.valueOf(role.toUpperCase()));
		userService.updateUserRole(user);
		return new ResponseEntity<>(Map.of("message", "Successfully change to new role"), HttpStatus.OK);
	}
}
