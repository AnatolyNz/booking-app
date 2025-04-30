package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String EMAIL_1 = "user@example.com";
    private static final String PASSWORD_1 = "password123";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test to avoid interference
        reset(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void register_UserExists_ThrowsRegistrationException() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto()
                .setEmail("user@example.com")
                .setPassword("password123")
                .setRepeatPassword("password123")
                .setFirstName("John")
                .setLastName("Doe");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(request));

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should register user when user does not exist")
    void register_NewUser_ReturnsUserResponseDto() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto()
                .setEmail("user@example.com")
                .setPassword("password123")
                .setRepeatPassword("password123")
                .setFirstName("John")
                .setLastName("Doe");

        User userToSave = new User();
        userToSave.setEmail(request.getEmail());
        userToSave.setPassword("encodedPassword");
        userToSave.setFirstName(request.getFirstName());
        userToSave.setLastName(request.getLastName());
        userToSave.setRole(User.UserRole.USER);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(userToSave.getEmail());
        savedUser.setFirstName(userToSave.getFirstName());
        savedUser.setLastName(userToSave.getLastName());

        UserResponseDto expected = new UserResponseDto()
                .setId(1L)
                .setEmail(savedUser.getEmail())
                .setFirstName(savedUser.getFirstName())
                .setLastName(savedUser.getLastName());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserResponseDto actual = userService.register(request);

        assertEquals(expected, actual);
    }
}
