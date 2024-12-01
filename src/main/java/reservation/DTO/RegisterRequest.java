package reservation.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	@NotEmpty(message = "Username is required.")
	@Size(min = 3, max = 15, message = "Username must be between 3 and 15.")
	private String username;

	@NotEmpty(message = "Email is required.")
	@Email(message = "Email should be valid.")
	private String email;

	@NotEmpty(message = "Password is required.")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
	message="Password must be at least 8 characters, one uppercase, one digit, one special character.")
	private String password;
	
	@NotEmpty(message = "Confirm password is required.")
	private String confirmPassword;

	private String role = "GUEST";

}
