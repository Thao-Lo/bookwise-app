package reservation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	//interface provided by Spring for sending email - pom.xml
	private JavaMailSender mailSender;

	public void sendVerificationEmail(String toEmail, String verificationCode) {
		String subject = "Registration Confirmation";
		String body = "Thank you for registering! Use the following code to verify your email address: "
				+ verificationCode;
		
		//util class in Spring for creating simple email message
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail); //recipient 
		message.setSubject(subject); //subject
		message.setText(body); //text
		mailSender.send(message);
	}
}
