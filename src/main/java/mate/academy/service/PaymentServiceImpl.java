package mate.academy.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentCancelResponseDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final StripeCheckoutService stripeCheckoutService;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public Payment initiatePayment(Long bookingId,
                                   String sessionUrl,
                                   BigDecimal amountToPay) {
        Booking booking = bookingRepository
                .findById(bookingId).orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        String sessionId = UUID.randomUUID().toString();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setSessionUrl(sessionUrl);
        payment.setSessionId(sessionId);
        payment.setAmountToPay(amountToPay);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentBySessionId(String sessionId) {
        return Optional.ofNullable(paymentRepository.findBySessionId(sessionId))
                .orElseThrow(() -> new RuntimeException("Payment not found with session ID: "
                        + sessionId));
    }

    public List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByUserId(userId, pageable);
        return payments.stream().map(payment -> paymentMapper.toDto(payment))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getAllPayments(Pageable pageable) {
        List<Payment> payments = paymentRepository.findAllPayments(pageable);
        return payments.stream().map(payment -> paymentMapper.toDto(payment))
                .collect(Collectors.toList());
    }

    @Override
    public Payment createAndReturnPaymentSession(Map<String, Object> bookingDetails,
                                       String successUrl,
                                       String cancelUrl) {
        try {
            Long bookingId = (Long) (bookingDetails.get("bookingId") instanceof Integer
                    ? Long.valueOf((Integer) bookingDetails.get("bookingId"))
                    : bookingDetails.get("bookingId"));

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            BigDecimal amount = booking.getAccommodation().getDailyRate()
                    .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(
                            booking.getCheckInDate(), booking.getCheckOutDate())));
            Long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData
                                                                    .ProductData.builder()
                                                                    .setName("Accommodation at "
                                                                            + booking
                                                                            .getAccommodation()
                                                                            .getLocation())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            Payment payment = initiatePayment(bookingId, session.getUrl(), amount);
            payment.setSessionId(session.getId());
            paymentRepository.save(payment);

            return paymentRepository.save(payment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Stripe session creation failed");
        }
    }

    @Override
    public String handlePaymentSuccess(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        updatePaymentStatus(payment.getId(), Payment.PaymentStatus.PAID);
        return "Payment success for session: " + sessionId;
    }

    @Override
    public PaymentCancelResponseDto handlePaymentCancel(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        updatePaymentStatus(payment.getId(), Payment.PaymentStatus.CANCELLED);
        return new PaymentCancelResponseDto(
                "Your payment was cancelled. You can retry within 24 hours.",
                payment.getSessionUrl()
        );
    }

    public Payment renewPaymentSession(Long paymentId, String successUrl,
                                       String cancelUrl) throws StripeException {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (existingPayment.getStatus() != Payment.PaymentStatus.CANCELLED
                && existingPayment.getStatus() != Payment.PaymentStatus.EXPIRED) {
            throw new RuntimeException("Only CANCELLED or EXPIRED payments can be renewed");
        }

        Booking booking = existingPayment.getBooking();
        BigDecimal amountToPay = existingPayment.getAmountToPay();

        Session newSession = stripeCheckoutService.createCheckoutSession(
                successUrl,
                cancelUrl,
                amountToPay.multiply(BigDecimal.valueOf(100)).longValue(), // Stripe uses cents
                "usd",
                String.valueOf(booking.getId())
        );

        Payment newPayment = new Payment();
        newPayment.setBooking(booking);
        newPayment.setStatus(Payment.PaymentStatus.PENDING);
        newPayment.setAmountToPay(amountToPay);
        newPayment.setSessionId(newSession.getId());
        newPayment.setSessionUrl(newSession.getUrl());

        return paymentRepository.save(newPayment);
    }
}
