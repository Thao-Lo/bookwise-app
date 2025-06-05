   package reservation.Repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import reservation.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

//	User findByEmail(String email);
	Optional<User> findByEmail(String email);
	
	User findByUsernameOrEmail(String username, String email);	
	
	Page<User> findAll(Pageable pageable);
	
//	boolean findByIdAndEmailVerifiedTrue(Long id); // return User
	Optional<User> findByIdAndEmailVerifiedTrue(Long id);
	
	
}
