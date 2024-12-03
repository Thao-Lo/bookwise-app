package reservation.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import reservation.Entity.User;
import reservation.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	public List<User> showAllUsers() {
		return userRepository.findAll();
	}

//	public Boolean isValidUsernameLength(String username) {
//		int length = username.length();
//		return length >= 3 && length <= 15;
//	}

	public Boolean isUsernameExist(String username) {
		return userRepository.findByUsername(username) != null;
			
	}
	public Boolean isEmailExist(String email) {
		return userRepository.findByEmail(email) != null;			
	}
	
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	public User findUserByUsernameOrEmail(String usernameorEmail) {
		return userRepository.findByUsernameOrEmail(usernameorEmail);
	}
//	public Boolean isValidEmailPattern(String email) {
//		String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,63}$";
//		Pattern pattern = Pattern.compile(emailPattern);
//		Matcher matcher = pattern.matcher(email);
//		return matcher.matches();
//	}
//	public Boolean isValidPasswordPattern(String password) {
//		String passwordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
//		Pattern pattern = Pattern.compile(passwordPattern);
//		Matcher matcher = pattern.matcher(password);
//		return matcher.matches();
//	}
	public String hashedPassword(String password) {
		BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
	public User saveUser(User user) {
		user.setPassword(hashedPassword(user.getPassword()));
		return userRepository.save(user);
	}
	public String generateVerificationCode() {
		return UUID.randomUUID().toString();
	}
}
