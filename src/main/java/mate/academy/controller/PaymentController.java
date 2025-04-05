package mate.academy.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.service.PaymentService;
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

    @GetMapping
    public Map<String, Object> getPaymentInformation(@RequestParam("user_id") Long userId) {
        return paymentService.getPaymentInformation(userId);
    }

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
