package reservation.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

@Configuration
public class StripeConfig {

	@Value("${stripe.api.secret-key}")
	private String secrectKey;
	
	@Bean
	 public String configureStripe() {
		Stripe.apiKey = secrectKey;
		return "Stripe Configured";
	}
}
