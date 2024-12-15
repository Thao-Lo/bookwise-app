package reservation.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import reservation.Entity.User;
import reservation.Entity.User.Role;
import reservation.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<User> showAllUsers() {
		return userRepository.findAll();
	}
	public Page<User> showAllUsers(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return userRepository.findAll(pageable);
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
	public Boolean isEmailVerified(Long id) {
		return userRepository.findByIdAndEmailVerifiedTrue(id).isPresent();			
	}
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	public User findUserByUsernameOrEmail(String usernameorEmail) {
		return userRepository.findByUsernameOrEmail(usernameorEmail, usernameorEmail);
	}
	
	public User findUserById(Long id) {		
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}
	
	public boolean isValidRole(String role) {
		for(Role r : Role.values()) {
			if(r.name().equalsIgnoreCase(role)) {
				return true;
			}
		}
		return false;
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
//		BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
	public User saveUser(User user) {			
		return userRepository.save(user);
	}
	@Transactional
	public User updateUserRole(User user) {
		return userRepository.save(user);
	}
	public String generateVerificationCode() {
		return UUID.randomUUID().toString();
	}
}
