package reservation.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
	UserService userService;
	@Autowired
	JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
		User user = userService.findUserByUsernameOrEmail(request.getUsernameOrEmail());

		if (user == null) {
			return new ResponseEntity<>(Map.of("error", "Invalid Username or Email."), HttpStatus.BAD_REQUEST);
		}
		// compare raw password with hashed password in db -> matches
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(request.getPassword(), user.getPassword())) {
			return new ResponseEntity<>(Map.of("error", "Incorrect password."), HttpStatus.BAD_REQUEST);
		}
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().toString());
		String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().toString());

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Login successfully.");
		response.put("accessToken", accessToken);
		response.put("refreshToken", refreshToken);
		response.put("user", new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/user/refresh-token")
	public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
		String refreshToken = request.get("refreshToken");
		try {
			Claims claims = jwtUtil.validateToken(refreshToken); // will throw exception here
			String username = claims.getSubject();
			String role = claims.get("role", String.class);

			String newAccessToken = jwtUtil.generateAccessToken(username, role);
//			Map<String, String> response = new HashMap<>();
//			response.put("accessToken", newAccessToken);
			return new ResponseEntity<>(Map.of("accessToken", newAccessToken), HttpStatus.OK);
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>(Map.of("error", "Token expired"), HttpStatus.UNAUTHORIZED);
		} catch (JwtException e) {
			return new ResponseEntity<>(Map.of("error", "Invalid Token"), HttpStatus.UNAUTHORIZED);
		}
	}
}
