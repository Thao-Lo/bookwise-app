package reservation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reservation.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	
}
