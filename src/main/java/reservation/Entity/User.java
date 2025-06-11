package reservation.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reservation.Enum.AuthProvider;


@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, unique = true, length = 255)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private AuthProvider provider;
	
	@Column(name = "provider_id")
	private String providerId;
		
	@Column(name = "reset_token")
	private String resetToken;

	@Column(name = "reset_token_expiration")
	private LocalDateTime resetTokenExpiration;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(name = "created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Column(name="verification_code")
	private String verificationCode;
	
	@Column(name = "email_verified", nullable = false)
	private boolean emailVerified = false;
	
	@Column(name="code_expiration_time", length = 64)
	private LocalDateTime codeExpirationTime;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	List<GuestReservation> guestReservations = new ArrayList<>();

	public enum Role {
		GUEST, ADMIN}

	// for Unit test
	public User(String username, String email) {
		super();
		this.username = username;
		this.email = email;
	}


}
