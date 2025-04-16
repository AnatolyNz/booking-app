package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.model.Payment;
import mate.academy.model.User;
import mate.academy.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping()
    @Operation(summary = "Retrieve payments", description = "Get payment information "
            + "for users or all payments for admins")
    public List<PaymentDto> getPayments(@AuthenticationPrincipal User currentUser,
                                        @RequestParam(value = "userId",
                                                required = false) Long userId,
                                        Pageable pageable) {
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && userId != null) {
            return paymentService.getAllPayments(pageable);
        } else {
            return paymentService.getPaymentsByUserId(currentUser.getId(), pageable);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create payment session", description
            = "Create a Stripe Checkout session for a booking")
    public ResponseEntity<PaymentResponseDto> createPaymentSession(
            @RequestBody Map<String, Object> bookingDetails,
            HttpServletRequest request) {

        String baseUrl = UriComponentsBuilder
                .fromHttpUrl(request.getRequestURL().toString())
                .replacePath(request.getContextPath())
                .build()
                .toUriString();

        String successUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/payments/cancel")
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
    public ResponseEntity<String> handlePaymentCancel(
            @RequestParam("session_id") String sessionId) {
        String result = paymentService.handlePaymentCancel(sessionId);
        return ResponseEntity.ok(result);
    }
}
