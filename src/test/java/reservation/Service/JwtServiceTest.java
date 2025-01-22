package reservation.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JwtServiceTest {

	private final JwtService jwtService = new JwtService();
	
	@Test
	void testExtractAccessToken_WithBearer() {
		//Arrange
		String token = "Bearer abc1234";
		
		//act
		String result = jwtService.extractAccessToken(token);
		
		//Assert
		assertEquals("abc1234", result);
	}
}
