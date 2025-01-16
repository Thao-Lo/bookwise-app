package reservation.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import reservation.DTO.LoginRequest;
import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Service.JwtService;
import reservation.Service.RedisService;
import reservation.Service.UserService;
import reservation.Utils.JwtUtil;

@RestController
@RequestMapping("/api/v1")
public class LoginController extends BaseController{
	@Autowired
	JwtUtil jwtUtil;	
	@Autowired
	PasswordEncoder passwordEncoder;	
	@Autowired
	JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
		// from user input either username or email
		User user = userService.findUserByUsernameOrEmail(request.getUsernameOrEmail());
		if (user == null) {
			return new ResponseEntity<>(Map.of("error", "Invalid Username or Email."), HttpStatus.BAD_REQUEST);
		}
		if (!userService.isEmailVerified(user.getId())) {
			return new ResponseEntity<>(Map.of("error", "Email not verified.Please Verified your email before login."),
					HttpStatus.BAD_REQUEST);
		}
		System.out.println("Login password: " + request.getPassword());
		System.out.println("Password in DB (hashed): " + user.getPassword());
		System.out.println("Password matches: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));

		// compare raw password with hashed password in db -> matches
		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return new ResponseEntity<>(Map.of("error", "Incorrect password."), HttpStatus.BAD_REQUEST);
		}
		//generate Tokens, store username and role in token
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().toString());
		String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().toString());

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Login successfully.");
		response.put("accessToken", accessToken);
		response.put("refreshToken", refreshToken);
		response.put("user",
				new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// refresh accessToken if refreshToken is still valid
	@PostMapping("/refresh-token")
	public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
		String refreshToken = request.get("refreshToken");
		try {
			if (redisService.isTokenBlacklist(refreshToken)) {
				return new ResponseEntity<>(Map.of("error", "Refresh Token is invalid or blacklist"),
						HttpStatus.UNAUTHORIZED);
			}
			Claims claims = jwtUtil.validateToken(refreshToken); // will throw exception here
			// extract username and role
			String username = claims.getSubject();
			String role = claims.get("role", String.class);

			String newAccessToken = jwtUtil.generateAccessToken(username, role);			
			return new ResponseEntity<>(Map.of("accessToken", newAccessToken), HttpStatus.OK);
			
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>(Map.of("error", "Token expired"), HttpStatus.UNAUTHORIZED);
		} catch (JwtException e) {
			return new ResponseEntity<>(Map.of("error", "Invalid Token"), HttpStatus.UNAUTHORIZED);
		}
	}

	// if user logout, token will store in Blacklist in Redis to revent hacker
	@PostMapping("/user/logout")
	public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> request) {
		System.out.println("authHeader" + authHeader);
		// get token from header
		String token = jwtService.extractAccessToken(authHeader);
		String refreshToken = request.get("refreshToken");
		
		// check token expired time
		long refreshTokenTTL = jwtUtil.getRemainingTokenTTL(refreshToken);
		long accessTokenTTL = jwtUtil.getRemainingTokenTTL(token);
		
		//add to Redis blacklist
		redisService.blacklistToken(token, accessTokenTTL);
		redisService.blacklistToken(refreshToken, refreshTokenTTL);

		return new ResponseEntity<>(Map.of("message", "Logout successfully."), HttpStatus.OK);
	}

	// to get user information if they have accessToken
	@GetMapping("/user/profile")
	public ResponseEntity<Object> getUserProfile(Principal principal) {
		checkPrincipal(principal, "You need to login to see your profile.");
//		if (principal == null) {
//			return new ResponseEntity<>(Map.of("error", "You need to login to see your profile."),
//					HttpStatus.UNAUTHORIZED);
//		}
		
		String username = principal.getName();
		User user = userService.findUserbyUsername(username);
		if (user == null) {
			return new ResponseEntity<>(Map.of("error", "User is not exist."), HttpStatus.BAD_REQUEST);
		}
		UserResponse userResponse = new UserResponse();
		userResponse.setUsername(username);
		userResponse.setId(user.getId());
		userResponse.setEmail(user.getEmail());
		userResponse.setRole(user.getRole().name());

		return new ResponseEntity<>(Map.of("user", userResponse), HttpStatus.OK);
	}
}
