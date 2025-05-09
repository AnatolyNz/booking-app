package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.payment.PaymentCancelResponseDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentResponseDto;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.service.PaymentService;
import mate.academy.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup(@Autowired WebApplicationContext applicationContext) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        teardown(dataSource); // Clear previous test state

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/bookings/add-bookings-all.sql")
            );
        }
    }

    @AfterEach
    void teardown() throws Exception {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/cleanup.sql"));
        }
    }

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all payments for admin")
    void getAllPayments_Admin_ReturnsList() throws Exception {
        PaymentDto payment1 = new PaymentDto();
        payment1.setId(1L);
        payment1.setAmountToPay(new BigDecimal("100.0"));
        payment1.setBookingId(1L);
        payment1.setUserId(1L);
        payment1.setSessionUrl("session123");
        payment1.setStatus(PaymentDto.PaymentStatus.PENDING);

        PaymentDto payment2 = new PaymentDto();
        payment2.setId(2L);
        payment2.setAmountToPay(new BigDecimal("150.0"));
        payment2.setBookingId(1L);
        payment2.setUserId(1L);
        payment2.setSessionUrl("session124");
        payment2.setStatus(PaymentDto.PaymentStatus.PAID);

        List<PaymentDto> mockedPayments = Arrays.asList(payment1, payment2);

        when(paymentService.getAllPayments(any(Pageable.class)))
                .thenReturn(mockedPayments);

        MvcResult result = mockMvc.perform(get("/payments")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        PaymentDto[] payments = objectMapper.readValue(responseBody, PaymentDto[].class);

        assertTrue(payments.length > 0, "Expected payments to be returned, but none were found.");

        verify(paymentService, times(1)).getAllPayments(any(Pageable.class));
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Get all payments for user")
    void getAllPayments_User_ReturnsUserPayments() throws Exception {
        PaymentDto payment1 = new PaymentDto();
        payment1.setId(1L);
        payment1.setAmountToPay(new BigDecimal("100.0"));
        payment1.setBookingId(1L);
        payment1.setUserId(1L);
        payment1.setSessionUrl("session123");
        payment1.setStatus(PaymentDto.PaymentStatus.PENDING);

        PaymentDto payment2 = new PaymentDto();
        payment2.setId(2L);
        payment2.setAmountToPay(new BigDecimal("150.0"));
        payment2.setBookingId(1L);
        payment2.setUserId(1L);
        payment2.setSessionUrl("session124");
        payment2.setStatus(PaymentDto.PaymentStatus.PAID);

        List<PaymentDto> mockedPayments = Arrays.asList(payment1, payment2);

        when(paymentService.getPaymentsByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(mockedPayments);

        MvcResult result = mockMvc.perform(get("/payments")
                        .param("userId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        PaymentDto[] payments = objectMapper.readValue(responseBody, PaymentDto[].class);

        assertTrue(payments.length > 0, "Expected payments to be returned, but none were found.");

        verify(paymentService, times(1)).getPaymentsByUserId(eq(1L),
                any(Pageable.class));
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Create payment session for USER")
    void createPaymentSession_User() throws Exception {
        Map<String, Object> bookingDetails = new HashMap<>();
        bookingDetails.put("bookingId", 1);
        bookingDetails.put("amountToPay", 100.0);
        bookingDetails.put("userId", 1);

        // Mock Payment object
        Payment mockPayment = new Payment();
        mockPayment.setSessionId("session123");
        mockPayment.setSessionUrl("https://stripe.com/checkout");
        mockPayment.setAmountToPay(new BigDecimal("100.0"));
        mockPayment.setStatus(Payment.PaymentStatus.PENDING);
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        mockPayment.setBooking(mockBooking);

        // Mock the service call
        when(paymentService.createAndReturnPaymentSession(any(), anyString(), anyString()))
                .thenReturn(mockPayment);

        MvcResult result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PaymentResponseDto responseDto = objectMapper.readValue(responseBody,
                PaymentResponseDto.class);

        assertNotNull(responseDto);
        assertEquals("session123", responseDto.getSessionId());
        assertEquals("https://stripe.com/checkout", responseDto.getSessionUrl());
        assertEquals(1L, responseDto.getBookingId());
        assertEquals("100.0", responseDto.getAmountToPay().toString());
        assertEquals("PENDING", responseDto.getStatus());

        verify(paymentService, times(1))
                .createAndReturnPaymentSession(eq(bookingDetails),
                        anyString(), anyString());
    }

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @Test
    @DisplayName("Create payment session for ADMIN")
    void createPaymentSession_Admin() throws Exception {
        Map<String, Object> bookingDetails = new HashMap<>();
        bookingDetails.put("bookingId", 1);
        bookingDetails.put("amountToPay", 150.0);
        bookingDetails.put("userId", 2);

        Payment mockPayment = new Payment();
        mockPayment.setSessionId("session456");
        mockPayment.setSessionUrl("https://stripe.com/checkout");
        mockPayment.setAmountToPay(new BigDecimal("150.0"));
        mockPayment.setStatus(Payment.PaymentStatus.PENDING);
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        mockPayment.setBooking(mockBooking);

        when(paymentService.createAndReturnPaymentSession(any(), anyString(), anyString()))
                .thenReturn(mockPayment);

        MvcResult result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PaymentResponseDto responseDto = objectMapper.readValue(responseBody,
                PaymentResponseDto.class);

        assertNotNull(responseDto);
        assertEquals("session456", responseDto.getSessionId());
        assertEquals("https://stripe.com/checkout", responseDto.getSessionUrl());
        assertEquals(1L, responseDto.getBookingId());
        assertEquals("150.0", responseDto.getAmountToPay().toString());
        assertEquals("PENDING", responseDto.getStatus());

        verify(paymentService, times(1))
                .createAndReturnPaymentSession(eq(bookingDetails), anyString(), anyString());
    }

    @Test
    public void testHandlePaymentSuccess() throws Exception {
        String sessionId = "session123";
        String expectedResult = "Payment successful for session " + sessionId;

        when(paymentService.handlePaymentSuccess(sessionId)).thenReturn(expectedResult);

        mockMvc.perform(get("/payments/success")
                        .param("session_id", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }

    @Test
    public void testHandlePaymentCancel() throws Exception {
        String sessionId = "session123";
        String expectedMessage = "Payment cancelled for session " + sessionId;
        String retryUrl = "http://localhost:80/payments/retry?session_id=" + sessionId;

        PaymentCancelResponseDto mockResponse =
                new PaymentCancelResponseDto(expectedMessage, retryUrl);

        when(paymentService.handlePaymentCancel(sessionId)).thenReturn(mockResponse);

        mockMvc.perform(get("/payments/cancel")
                        .param("session_id", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\""
                        + expectedMessage + "\",\"retryUrl\":\"" + retryUrl + "\"}"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testRenewPaymentSession_userRole() throws Exception {
        String sessionId = "newSession123";
        String sessionUrl = "https://stripe.com/checkout";

        Booking mockBooking = new Booking();
        mockBooking.setId(10L);

        BigDecimal amountToPay = BigDecimal.valueOf(150);
        Payment mockPayment = new Payment();
        mockPayment.setSessionId(sessionId);
        mockPayment.setSessionUrl(sessionUrl);
        mockPayment.setAmountToPay(amountToPay);
        mockPayment.setStatus(Payment.PaymentStatus.PENDING);
        mockPayment.setBooking(mockBooking);

        when(paymentService.renewPaymentSession(anyLong(), anyString(),
                anyString())).thenReturn(mockPayment);

        Long paymentId = 1L;

        mockMvc.perform(post("/payments/{paymentId}/renew", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.sessionUrl").value(sessionUrl))
                .andExpect(jsonPath("$.amountToPay").value(150))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRenewPaymentSession_adminRole() throws Exception {
        String sessionId = "newSession123";
        String sessionUrl = "https://stripe.com/checkout";

        Booking mockBooking = new Booking();
        mockBooking.setId(1L);

        BigDecimal amountToPay = BigDecimal.valueOf(150);
        Payment mockPayment = new Payment();
        mockPayment.setSessionId(sessionId);
        mockPayment.setSessionUrl(sessionUrl);
        mockPayment.setAmountToPay(amountToPay);
        mockPayment.setStatus(Payment.PaymentStatus.PENDING);
        mockPayment.setBooking(mockBooking);

        when(paymentService.renewPaymentSession(anyLong(), anyString(),
                anyString())).thenReturn(mockPayment);

        Long paymentId = 1L;

        mockMvc.perform(post("/payments/{paymentId}/renew", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.sessionUrl").value(sessionUrl))
                .andExpect(jsonPath("$.amountToPay").value(amountToPay))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    public void testRenewPaymentSession_unauthenticated() throws Exception {
        Long paymentId = 1L;

        mockMvc.perform(post("/payments/{paymentId}/renew", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
