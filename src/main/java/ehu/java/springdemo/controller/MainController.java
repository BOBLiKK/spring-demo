package ehu.java.springdemo.controller;

import ehu.java.springdemo.dto.UserRegistrationDto;
import ehu.java.springdemo.dto.UserResponseDto;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.event.UserLoginEvent;
import ehu.java.springdemo.event.UserRegistrationEvent;
import ehu.java.springdemo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import static ehu.java.springdemo.constant.PageNameConstant.*;
import static ehu.java.springdemo.constant.AttrbiuteNameConstant.*;

@Slf4j
@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(TITLE, MAIN_PAGE);
        return INDEX_PAGE;
    }

    @GetMapping("/login")
    public String login() {
        return LOGIN_PAGE;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute(USER, new UserRegistrationDto());
        return REGISTER_PAGE;
    }

    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute(USER) @Valid UserRegistrationDto userDto, BindingResult bindingResult) {
        if (userService.validateRegistration(userDto, bindingResult)) {
            return REGISTER_PAGE;
        }
        userService.registerNewUser(userDto);
        eventPublisher.publishEvent(new UserRegistrationEvent(this, userDto.getLogin()));
        return REDIRECT_LOGIN;
    }

    @GetMapping("/user/info")
    @ResponseBody
    public UserResponseDto getUserInfo(Authentication authentication) {
        return userService.getUserInfo(authentication.getName());
    }

    @GetMapping("/redirect")
    public String redirectAfterLogin(HttpSession session, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return REDIRECT_LOGIN;
        }
        User user = userService.findUserByLogin(authentication.getName());
        if (user == null) {
            return REDIRECT_LOGIN;
        }
        session.setAttribute(CURRENT_USER, user);
        String username = authentication.getName();
        eventPublisher.publishEvent(new UserLoginEvent(this, username, user.getRole()));
        return switch (user.getRole()) {
            case ADMIN_CAPS -> REDIRECT_ADMIN_DASHBOARD;
            case USER_CAPS -> REDIRECT_USER_DASHBOARD;
            default -> REDIRECT;
        };
    }
}
