package reservation.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import reservation.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
}
