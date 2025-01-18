package reservation.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Entity.User.Role;


@RestController
@RequestMapping("api/v1/admin/user")
public class AdminUserController extends BaseController {

	@GetMapping("/list")
	public ResponseEntity<Object> getUserList(Principal principal,
			@RequestParam(required = false, defaultValue = "false") boolean pageable,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(10) int size) {
		
		checkPrincipal(principal, "Not authorized." );
		
		if (pageable) {
			Page<User> users = userService.showAllUsers(page, size);
			// from Base Controller
			checkPageNotEmpty(users, "No users found.");
			
			// map Stream<User> thành Stream<UserResponse>
			List<UserResponse> userPage = users.getContent().stream().map(
					user -> {
						UserResponse userResponse = new UserResponse();
						userResponse.setId(user.getId());
						userResponse.setUsername(user.getUsername());
						userResponse.setEmail(user.getEmail());
						userResponse.setRole(user.getRole().name());
						return userResponse;
					}).toList();
			return new ResponseEntity<>(Map.of(
					"users" , userPage,
					"currentPage", users.getNumber(),
					"usersPerPage", users.getNumberOfElements(),
					"totalPages", users.getTotalPages(),
					"totalUsers", users.getTotalElements()), HttpStatus.OK);
		}
		List<User> users = userService.showAllUsers();
		checkListNotEmpty(users,"No users found.");
		
		List<UserResponse> userList = users.stream().map(
				user -> {
					UserResponse userResponse = new UserResponse();
					userResponse.setId(user.getId());
					userResponse.setUsername(user.getUsername());
					userResponse.setEmail(user.getEmail());
					userResponse.setRole(user.getRole().name());
					return userResponse;
				}).toList();
				
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@PostMapping("/edit-role/{id}/{role}")
	public ResponseEntity<Object> editUserRole(Principal principal, @PathVariable Long id, @PathVariable String role) {
		checkPrincipal(principal, "Not authorized." );
		if (!userService.isValidRole(role)) {
			return new ResponseEntity<>(Map.of("error", String.format("Invalid role provided: %s.", role)), HttpStatus.BAD_REQUEST);
		}
		
		User user = userService.findUserById(id);
		if(user.getRole().name().equalsIgnoreCase(role)) {
			return new ResponseEntity<>(Map.of("message", "Change is not neccessary."), HttpStatus.OK);
		}
		String oldRole = user.getRole().name();
		user.setRole(Role.valueOf(role.toUpperCase()));
		userService.updateUserRole(user);
		return new ResponseEntity<>(Map.of("message", String.format("Successfully changed %s from role: %s to new role: %s ", user.getUsername(),oldRole, user.getRole().name())), HttpStatus.OK);
	}
}
