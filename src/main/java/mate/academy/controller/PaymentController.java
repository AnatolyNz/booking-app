package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.model.User;
import mate.academy.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payment")
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String createPaymentSession(@RequestBody Map<String, Object> bookingDetails) {
        return paymentService.createPaymentSession(bookingDetails);
    }

    @GetMapping("/success")
    public String handlePaymentSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.handlePaymentSuccess(sessionId);
    }

    @GetMapping("/cancel")
    public String handlePaymentCancel(@RequestParam("session_id") String sessionId) {
        return paymentService.handlePaymentCancel(sessionId);
    }
}
