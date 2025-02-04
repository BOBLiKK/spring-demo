package ehu.java.springdemo.controller;

import ehu.java.springdemo.dto.UserRegistrationDto;
import ehu.java.springdemo.dto.UserResponseDto;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Main page");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }



    /*
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute("user") User user, Model model) {
        try {
            if (userService.findUserByLogin(user.getLogin()) != null) {
                model.addAttribute("error", "User with this name already exists");
                return "register";
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(User.Role.USER);
            userService.save(user);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration error. Try again later.");
            return "register";
        }
    }
     */

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute("user") @Valid UserRegistrationDto userDto, BindingResult bindingResult, Model model) {
        if (userService.findUserByLogin(userDto.getLogin()) != null) {
            bindingResult.rejectValue("login", "error.user", "This login has already taken. ");
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match. ");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Создаем пользователя и сохраняем его
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(User.Role.USER);
        userService.save(user);

        return "redirect:/login";
    }


    @GetMapping("/user/info")
    @ResponseBody
    public UserResponseDto getUserInfo(Authentication authentication) {
        User user = userService.findUserByLogin(authentication.getName());
        return new UserResponseDto(user);
    }


    @GetMapping("/redirect")
    public String redirectAfterLogin(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String currentUserName = authentication.getName();
        User user = userService.findUserByLogin(currentUserName);

        if (user == null) {
            return "redirect:/login";
        }

        session.setAttribute("currentUser", user);
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/admin_dashboard";
        } else if ("USER".equals(user.getRole())) {
            return "redirect:/user/user_dashboard";
        }
        return "redirect:/";
    }




}
