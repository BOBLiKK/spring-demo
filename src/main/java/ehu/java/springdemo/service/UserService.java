package ehu.java.springdemo.service;

import ehu.java.springdemo.dto.UserRegistrationDto;
import ehu.java.springdemo.dto.UserResponseDto;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {


    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Long getUserIdByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean validateRegistration(UserRegistrationDto userDto, BindingResult bindingResult) {
        if (findUserByLogin(userDto.getLogin()) != null) {
            bindingResult.rejectValue("login", "error.user", "This login has already been taken.");
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match.");
        }
        return bindingResult.hasErrors();
    }

    public void registerNewUser(UserRegistrationDto userDto) {
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setRole(User.Role.USER);
        save(user);
    }

    public UserResponseDto getUserInfo(String username) {
        User user = findUserByLogin(username);
        return new UserResponseDto(user);
    }
}



