package reservation.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class StripeService {
	private static final Logger logger = Logger.getLogger(StripeService.class.getName());

	public PaymentIntent createPaymentIntent(Long amount, String email, String sessionId) throws StripeException {

		return PaymentIntent.create(
				PaymentIntentCreateParams.builder()
				.setAmount(amount)
				.setCurrency("aud")
				.setReceiptEmail(email)
				.putMetadata("sessionId", sessionId)
				.build());	
	}

	public boolean cancelPaymentIntent(String paymentIntentId, String cancellationReason) throws StripeException {
	    // Retrieve the PaymentIntent
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		
		if (paymentIntent == null) {
			return false;
		}
		 // Determine the cancellation reason
		String reasonToCancel = (cancellationReason != null && !cancellationReason.isEmpty())
				? cancellationReason : "Testing in progress";
		
		PaymentIntentCancelParams.Builder params = PaymentIntentCancelParams.builder();

		PaymentIntentCancelParams.CancellationReason reason = PaymentIntentCancelParams.CancellationReason
				.valueOf(reasonToCancel);
		if (reason != null) {
			params.setCancellationReason(reason);
		} else {
			logger.warning("Invalid cancellation reason provided: " + reasonToCancel);
		}
		paymentIntent.cancel(params.build());

		return true;
	}

}
