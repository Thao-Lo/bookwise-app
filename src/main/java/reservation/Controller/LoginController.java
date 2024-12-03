package reservation.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reservation.DTO.LoginRequest;
import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Service.UserService;
import reservation.Utils.JwtUtil;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private JwtUtil jwtUtil;

	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
		User user = userService.findUserByUsernameOrEmail(request.getUsernameOrEmail());

		if (user == null) {
			return new ResponseEntity<>("Invalid Username or Email", HttpStatus.BAD_REQUEST);
		}
		// compare raw password with hashed password in db -> matches
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(request.getPassword(), user.getPassword())) {
			return new ResponseEntity<>("Incorrect password.", HttpStatus.BAD_REQUEST);
		}
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().toString());
		String refreshToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().toString());
		
		Map<String, Object> response = new HashMap<>();		
		response.put("message", "Login successfully.");
		response.put("access token", accessToken);
		response.put("refresh token", refreshToken);
		response.put("user", new UserResponse(user.getEmail(), user.getUsername(), user.getRole().name()));		
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
