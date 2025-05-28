package mate.academy.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentCancelResponseDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.model.Payment;
import mate.academy.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payment management", description = "Endpoints for managing payment")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Retrieve payments", description =
            "Get payment information for users or all payments for admins")
    public Page<PaymentDto> getPayments(Authentication authentication,
                                        @RequestParam(value = "userId",
                                                required = false) Long userId,
                                        Pageable pageable) {
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (isAdmin && userId == null) {
            return paymentService.getAllPayments(pageable);
        } else {
            return paymentService.getPaymentsByUserId(userId, pageable);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create payment session", description
            = "Create a Stripe Checkout session for a booking")
    public ResponseEntity<PaymentResponseDto> createPaymentSession(
            @RequestBody Map<String, Object> bookingDetails,
            HttpServletRequest request) {

        String baseUrl = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .build()
                .toUriString();

        String successUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/payments/cancel")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        Payment payment = paymentService.createAndReturnPaymentSession(bookingDetails,
                successUrl, cancelUrl);

        PaymentResponseDto responseDto = new PaymentResponseDto(
                payment.getSessionId(),
                payment.getSessionUrl(),
                payment.getBooking().getId(),
                payment.getAmountToPay(),
                payment.getStatus().name()
        );

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/success")
    @Operation(summary = "Stripe success", description = "Handle successful Stripe payment")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId) {
        String result = paymentService.handlePaymentSuccess(sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cancel")
    @Operation(summary = "Stripe cancel", description = "Handle cancelled Stripe payment")
    public ResponseEntity<PaymentCancelResponseDto> handlePaymentCancel(
            @RequestParam("session_id") String sessionId) {
        PaymentCancelResponseDto response = paymentService
                .handlePaymentCancel(sessionId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{paymentId}/renew")
    @Operation(summary = "Renew payment session", description
            = "Renew a Stripe Checkout session for an expired or cancelled payment")
    public ResponseEntity<PaymentResponseDto> renewPaymentSession(
            @PathVariable Long paymentId,
            HttpServletRequest request) throws StripeException {

        String baseUrl = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .build()
                .toUriString();

        String successUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/api/payments/cancel")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        Payment payment = paymentService.renewPaymentSession(paymentId, successUrl, cancelUrl);

        PaymentResponseDto responseDto = new PaymentResponseDto(
                payment.getSessionId(),
                payment.getSessionUrl(),
                payment.getBooking().getId(),
                payment.getAmountToPay(),
                payment.getStatus().name()
        );

        return ResponseEntity.ok(responseDto);
    }
}
