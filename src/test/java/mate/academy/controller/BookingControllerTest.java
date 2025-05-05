package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.User;
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
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cleanup.sql")
            );
        }
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Get all bookings")
    void getAllBookings_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        BookingDto[] bookings = objectMapper.readValue(responseBody, BookingDto[].class);

        assertTrue(bookings.length > 0);
    }

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    @Test
    @DisplayName("Admin gets bookings by userId")
    void getBookings_AdminWithUserId_ShouldReturnBookings() throws Exception {
        mockMvc.perform(get("/bookings/my")
                        .param("userId", "1")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Get booking by ID - success")
    void getBookingById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        BookingDto actual = objectMapper.readValue(responseBody, BookingDto.class);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals("PENDING", actual.getStatus().toString());
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Create booking successfully")
    void createBooking_ValidRequest_ReturnsCreatedBooking() throws Exception {
        CreateBookingRequestDto request = new CreateBookingRequestDto();
        request.setUserId(1L);
        request.setAccommodationId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(12));
        request.setCheckOutDate(LocalDate.now().plusDays(14));
        request.setStatus(CreateBookingRequestDto.BookingStatus.PENDING);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("password");
        mockUser.setRole(User.UserRole.USER);

        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Cancel booking by ID")
    void cancelBooking_ValidId_NoContent() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Access denied for unauthenticated users")
    void getBookings_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @Test
    @DisplayName("Get bookings by user ID and status - success")
    void getBookingsByUserIdAndStatus_ValidParams_ReturnsList() throws Exception {
        mockMvc.perform(get("/bookings/{userId}/{status}", 1L, "PENDING")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @WithMockUser(username = "user@example.com", roles = {"USER"})
    @Test
    @DisplayName("Update booking - success")
    void updateBooking_ValidRequest_ReturnsUpdatedBooking() throws Exception {
        CreateBookingRequestDto request = new CreateBookingRequestDto();
        request.setUserId(1L);
        request.setAccommodationId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(15));
        request.setCheckOutDate(LocalDate.now().plusDays(17));
        request.setStatus(CreateBookingRequestDto.BookingStatus.CONFIRMED);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("password");
        mockUser.setRole(User.UserRole.USER);

        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/bookings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
