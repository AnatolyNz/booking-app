package mate.academy.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import mate.academy.exception.StripeSessionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeSessionException("Failed to retrieve Stripe session: " + sessionId, e);
        }
    }
}
