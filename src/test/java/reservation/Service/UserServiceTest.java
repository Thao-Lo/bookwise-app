package reservation.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import reservation.Entity.User;
import reservation.Repository.UserRepository;

//Allows the use of annotations like @Mock and @InjectMocks.
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock // Creates a mock object for UserRepository.
	private UserRepository userRepository;

	@InjectMocks // Automatically injects the mock (UserRepository) into the class under test
					// (UserService).
	private UserService userService;

	@Test
	void testIsUsernameExist_ReturnsTrue() {
		// Arrange - Prepare the data needed for the test.
		String username = "testUser";
		User mockUser = new User();
		mockUser.setUsername(username);

		// When userRepository.findByUsername("testUser") is called, it will return
		// mockUser.
		when(userRepository.findByUsername(username)).thenReturn(mockUser);

		// Act - Call the method under test
		boolean result = userService.isUsernameExist(username);

		// Assert - Verify the result using assertions True/false
		assertTrue(result);
		verify(userRepository, times(1)).findByUsername(username);

	}

	@Test
	void testIsUsernameExist_ReturnsFalse() {
		// Arrange
		String username = "nonExistentUser";
		when(userRepository.findByUsername(username)).thenReturn(null);

		// Act
		boolean result = userService.isUsernameExist(username);

		// Assert
		assertFalse(result);
		verify(userRepository, times(1)).findByUsername(username);
	}

	// Test Roles
	@Test
	void testIsValidRole_ReturnsTrueForAllRoles() {
		// Arrange & Act
		String[] roles = { "ADMIN", "GUEST" };
		for (String role : roles) {
			boolean result = userService.isValidRole(role);
			// Assert
			assertTrue(result);
		}
	}

	@Test
	void testIsValidRole_ReturnsFalse() {
		String role = "NADMIN";
		boolean result = userService.isValidRole(role);
		assertFalse(result);
	}

	// Test pagination showAllUser
	@Test
	void testShowAllUser() {
		// Arrange
		int page = 0;
		int size = 2;
		Pageable pageable = PageRequest.of(page, size);
		List<User> mockUsers = Arrays.asList(
				new User("user1", "user1@example.com"),
				new User("user2", "user2@example.com"));
		Page<User> mockPage = new PageImpl<>(mockUsers, pageable, mockUsers.size());
		when(userRepository.findAll(pageable)).thenReturn(mockPage);
		
		// Act
		Page<User> result = userService.showAllUsers(page, size);
		
		//Assert
		//getContent return List<User> == mockUsers
		assertEquals(2, result.getContent().size());
		assertEquals("user1", result.getContent().get(0).getUsername());
		assertEquals("user2", result.getContent().get(1).getUsername());
		verify(userRepository, times(1)).findAll(pageable);
		
		
	}
}
