package reservation.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "guest_reservation")
public class GuestReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id") // Reference to User
	private User user;

	@OneToOne
	@JoinColumn(name = "slot_id") // Reference to Slot
	private Slot slot;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Column(name = "reservation_time", nullable = false)
	@CreationTimestamp
	private LocalDateTime reservationTime;

	@Column(name = "number_of_guest", nullable = false)	
	private int numberOfGuests;

	public enum Status {
		BOOKED, CANCELLED
	}
}
