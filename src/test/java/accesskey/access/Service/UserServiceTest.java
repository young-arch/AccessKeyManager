package accesskey.access.Service;

import accesskey.access.Entity.Role;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.InvalidCredentialsException;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        assertNotNull(createdUser.getVerificationToken());
        assertNotNull(createdUser.getVerificationTokenExpirationTime());
        assertEquals(Role.SCHOOL_IT, createdUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(eq("test@example.com"), eq("Account Verification"), anyString());
    }

    @Test
    void loginUser() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act
        User loggedInUser = userService.loginUser(email, rawPassword);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void loginUser_withInvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(email, rawPassword));
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void confirmVerification() {
        // Arrange
        String token = "verificationToken";
        User user = new User();
        user.setVerificationToken(token);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().plusHours(1));
        user.setVerified(false);

        when(userRepository.findByVerificationToken(token)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.confirmVerification(token);

        // Assert
        assertTrue(user.getVerified());
        verify(userRepository, times(1)).findByVerificationToken(token);
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendEmail(eq(user.getEmail()), eq("Email Verification Successful"), anyString());
    }

    @Test
    void confirmVerification_withExpiredToken() {
        // Arrange
        String token = "expiredToken";
        User user = new User();
        user.setVerificationToken(token);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().minusHours(1));

        when(userRepository.findByVerificationToken(token)).thenReturn(user);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.confirmVerification(token));
        verify(userRepository, times(1)).findByVerificationToken(token);
    }
}
