package ehu.java.springdemo.service;

import ehu.java.springdemo.dto.UserRegistrationDto;
import ehu.java.springdemo.dto.UserResponseDto;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final String TEST_LOGIN = "testUser";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        User user = createTestUser();
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername(TEST_LOGIN);

        assertEquals(TEST_LOGIN, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + user.getRole())));
    }

    @Test
    void loadUserByUsername_UserNotExists_ThrowsException() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(TEST_LOGIN));
    }

    @Test
    void save_CallsRepositorySave() {
        User user = createTestUser();
        userService.save(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserByLogin_UserExists_ReturnsUser() {
        User user = createTestUser();
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(user));

        User result = userService.findUserByLogin(TEST_LOGIN);
        assertNotNull(result);
        assertEquals(TEST_LOGIN, result.getLogin());
    }

    @Test
    void findUserByLogin_UserNotExists_ReturnsNull() {
        when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());
        User result = userService.findUserByLogin("nonexistent");
        assertNull(result);
    }

    @Test
    void findUserByEmail_UserExists_ReturnsUser() {
        User user = createTestUser();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        User result = userService.findUserByEmail(TEST_EMAIL);
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
    }

    @Test
    void findUserByEmail_UserNotExists_ReturnsNull() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        User result = userService.findUserByEmail("nonexistent@example.com");
        assertNull(result);
    }

    @Test
    void getUserIdByLogin_UserExists_ReturnsUserId() {
        User user = createTestUser();
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(user));

        Long id = userService.getUserIdByLogin(TEST_LOGIN);
        assertEquals(user.getId(), id);
    }

    @Test
    void getUserIdByLogin_UserNotExists_ThrowsException() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserIdByLogin(TEST_LOGIN));
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        User user = createTestUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.findById(user.getId());
        assertNotNull(result);
        assertEquals(user.getLogin(), result.getLogin());
    }

    @Test
    void findById_UserNotExists_ReturnsNull() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        User result = userService.findById(999L);
        assertNull(result);
    }

    @Test
    void validateRegistration_LoginTaken_AddsError() {

        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(createTestUser()));

        UserRegistrationDto dto = createTestRegistrationDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "userRegistrationDto");

        boolean hasErrors = userService.validateRegistration(dto, bindingResult);
        assertTrue(hasErrors);
        assertTrue(bindingResult.hasFieldErrors("login"));
    }

    @Test
    void validateRegistration_PasswordsMismatch_AddsError() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());

        UserRegistrationDto dto = createTestRegistrationDto();
        dto.setConfirmPassword("differentPassword");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "userRegistrationDto");

        boolean hasErrors = userService.validateRegistration(dto, bindingResult);
        assertTrue(hasErrors);
        assertTrue(bindingResult.hasFieldErrors("confirmPassword"));
    }

    @Test
    void validateRegistration_NoErrors_ReturnsFalse() {
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.empty());

        UserRegistrationDto dto = createTestRegistrationDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "userRegistrationDto");

        boolean hasErrors = userService.validateRegistration(dto, bindingResult);
        assertFalse(hasErrors);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    void registerNewUser_EncodesPasswordAndSavesUser() {
        UserRegistrationDto dto = createTestRegistrationDto();
        userService.registerNewUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(TEST_LOGIN, savedUser.getLogin());
        assertEquals(TEST_EMAIL, savedUser.getEmail());
        assertNotEquals(TEST_PASSWORD, savedUser.getPassword());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPassword()));
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    void getUserInfo_ReturnsCorrectUserResponseDto() {
        User user = createTestUser();
        when(userRepository.findByLogin(TEST_LOGIN)).thenReturn(Optional.of(user));

        UserResponseDto responseDto = userService.getUserInfo(TEST_LOGIN);

        assertEquals(user.getId(), responseDto.getId());
        assertEquals(user.getLogin(), responseDto.getLogin());
        assertEquals(user.getEmail(), responseDto.getEmail());
        assertEquals(user.getRole(), responseDto.getRole());
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setLogin(TEST_LOGIN);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setEmail(TEST_EMAIL);
        user.setRole(User.Role.USER);
        return user;
    }

    private UserRegistrationDto createTestRegistrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLogin(TEST_LOGIN);
        dto.setPassword(TEST_PASSWORD);
        dto.setConfirmPassword(TEST_PASSWORD);
        dto.setEmail(TEST_EMAIL);
        return dto;
    }
}
