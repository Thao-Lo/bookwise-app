package reservation.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import reservation.DTO.LoginRequest;
import reservation.DTO.LoginResponse;
import reservation.DTO.UserResponse;
import reservation.Entity.User;
import reservation.Enum.ErrorCode;
import reservation.Exception.UserException;
import reservation.Service.JwtService;
import reservation.Utils.JwtUtil;

@RestController
@RequestMapping("/api/v1")
public class LoginController extends BaseController{
	//SLF4J
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
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
		logger.debug("Login request receive for username/email: {}", request.getUsernameOrEmail());
		if (user == null) {
			logger.warn("Loggin failed: User not found for username/email: {}",request.getUsernameOrEmail() );
			throw new UserException(ErrorCode.INVALID_USERNAME_OR_EMAIL, ("Invalid Username or Email for: " + request.getUsernameOrEmail()));	
		}
		if (!userService.isEmailVerified(user.getId())) {
			logger.warn("Login failed: Email not verified for user Id: {}", user.getId());
			throw new UserException(ErrorCode.UNVERIFIED_EMAIL, "Email not verified. Please Verified your email before login.");
		}		
		
		logger.debug("Password matches: {} ", passwordEncoder.matches(request.getPassword(), user.getPassword()));

		// compare raw password with hashed password in db -> matches
		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			logger.warn("Login failed: Incorrect password for user ID: {}", user.getId());
			throw new UserException(ErrorCode.PASSWORD_MIXMATCH, "Incorrect password.");
		}
		
		logger.info("User logged in successfully: {}", user.getUsername());
		//generate Tokens, store username and role in token
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().toString());
		String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().toString());
		logger.debug("Generate tokens for user Id: {}", user.getId());
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Login successfully.");
		response.put("accessToken", accessToken);
		response.put("refreshToken", refreshToken);
		response.put("user",
				new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/auth/token")
	public ResponseEntity<Object> getTokenFromSessionId(@Valid @RequestBody Map<String, String> request){
		String sessionId = request.get("sessionId");
		if(sessionId == null) {
			throw new UserException(ErrorCode.OAUTH2_SESSIONID_NOT_FOUND, "SessionId is not found, cannot retrieve user data.");
		}
		String key = "oauth2:sessionId:" + sessionId;
		
		LoginResponse loginResponse = (LoginResponse) redisService.getOauth2LoginDetails(key);		
		System.out.println("oauth2: " + loginResponse);
		redisService.deleteOauthKey(key);
		return ResponseEntity.ok(loginResponse);
	}
	
	
	// refresh accessToken if refreshToken is still valid
	@PostMapping("/refresh-token")
	public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
		String refreshToken = request.get("refreshToken");
		try {
			if (redisService.isTokenBlacklist(refreshToken)) {
				throw new UserException(ErrorCode.INVALID_TOKEN, "Refresh Token is invalid or blacklist");				
			}
			Claims claims = jwtUtil.validateToken(refreshToken); // will throw exception here
			// extract username and role
			String username = claims.getSubject();
			String role = claims.get("role", String.class);

			String newAccessToken = jwtUtil.generateAccessToken(username, role);			
			return new ResponseEntity<>(Map.of("accessToken", newAccessToken), HttpStatus.OK);
			
		} catch (ExpiredJwtException e) {
			logger.error("Token expired", e);
			throw new UserException(ErrorCode.INVALID_TOKEN, "Token expired");	
		} catch (JwtException e) {
			logger.error("Invalid Token", e);
			throw new UserException(ErrorCode.INVALID_TOKEN, "Invalid Token");
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
		
		String username = principal.getName();
		User user = userService.findUserbyUsername(username);
		if (user == null) {
			throw new UserException(ErrorCode.USER_NOT_FOUND, ("User is not exist for: " + username));
		}
		UserResponse userResponse = new UserResponse();
		userResponse.setUsername(username);
		userResponse.setId(user.getId());
		userResponse.setEmail(user.getEmail());
		userResponse.setRole(user.getRole().name());

		return new ResponseEntity<>(Map.of("user", userResponse), HttpStatus.OK);
	}
}
