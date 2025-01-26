package reservation.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import reservation.Utils.TimeZoneConverter;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
	@Mock
	private JavaMailSender mailSender;
	@Mock
	private TimeZoneConverter timeZoneConverter;
	@InjectMocks
	private EmailService emailService;

	@Test
	void testSendVerificationCodeToEmail() {
		// Arrange
		String email = "abc@gmail.com";
		String code = "1234";

		// Mock the mailSender behaviour
		doNothing().when(mailSender).send(any(SimpleMailMessage.class));

		// Act
		emailService.sendVerificationEmail(email, code);
		// Assert
		// create email captor object
		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		// capture the email when mailSender send it
		verify(mailSender, times(1)).send(messageCaptor.capture());

		SimpleMailMessage capturedMessage = messageCaptor.getValue();
		assertEquals(email, capturedMessage.getTo()[0]);
		assertEquals("Registration Confirmation", capturedMessage.getSubject());
		assertEquals("Thank you for registering! Use the following code to verify your email address: 1234",
				capturedMessage.getText());

	}

}
