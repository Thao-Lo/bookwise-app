package reservation.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	@NotEmpty(message = "Username or Email is required.")
	private String usernameOrEmail;
	
	@NotEmpty(message = "Password is required.")
	private String password;
}
