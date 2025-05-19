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
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.impl.UserServiceImpl;
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
    private RoleRepository roleRepository;
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

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

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

        Role role = new Role();
        role.setRoleName(Role.RoleName.USER);

        User mappedUser = new User();
        mappedUser.setEmail(request.getEmail());
        mappedUser.setFirstName(request.getFirstName());
        mappedUser.setLastName(request.getLastName());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(Role.RoleName.USER)).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(mappedUser.getEmail());
        savedUser.setFirstName(mappedUser.getFirstName());
        savedUser.setLastName(mappedUser.getLastName());
        savedUser.setRole(role);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto expected = new UserResponseDto()
                .setId(1L)
                .setEmail(savedUser.getEmail())
                .setFirstName(savedUser.getFirstName())
                .setLastName(savedUser.getLastName());

        when(userMapper.toDto(any(User.class))).thenReturn(expected);

        UserResponseDto actual = userService.register(request);

        assertEquals(expected, actual);
    }
}
