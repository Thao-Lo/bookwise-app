package reservation.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import reservation.Entity.GuestReservation;
import reservation.Utils.TimeZoneConverter;

@Service
public class EmailService {
	@Autowired
	// interface provided by Spring for sending email - pom.xml
	private JavaMailSender mailSender;
	@Autowired
	TimeZoneConverter timeZoneConverter;

	public void sendVerificationEmail(String toEmail, String verificationCode) {
		String subject = "Registration Confirmation";
		String body = "Thank you for registering! Use the following code to verify your email address: "
				+ verificationCode;

		// util class in Spring for creating simple email message
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail); // recipient
		message.setSubject(subject); // subject
		message.setText(body); // text
		mailSender.send(message);
	}

	public void sendBookingConfirmation(GuestReservation reservation) {
		String userEmail = reservation.getUser().getEmail();
		if (userEmail == null || userEmail.isEmpty()) {
			throw new IllegalArgumentException("User email is not valid");
		}

		LocalDateTime datetime = reservation.getSlot().getSchedule().getDatetime();
		String subject = "Booking Confirmation";
		String body = String.format(
				"Thank you for booking. Here are your details:\n- Guests: %d\n- Seat: %s\n- Time: %s on %s",
				reservation.getNumberOfGuests(),
				reservation.getSlot().getSeat().getSeatName(),
				datetime.toLocalTime(),
				datetime.toLocalDate());
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(reservation.getUser().getEmail());
		message.setSubject(subject);
		message.setText(body);
		try {
			mailSender.send(message);
		}catch(Exception e){
			throw new RuntimeException("Failed to send confirmation email", e);
		}
	}
}
