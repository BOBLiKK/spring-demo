package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.UserRepository;
import ehu.java.springdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute("user") User user, Model model) {
        try {
            if (userService.findUserByLogin(user.getLogin()) != null) {
                model.addAttribute("error", "This login already exists");
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

    @GetMapping("/redirect")
    public String redirectAfterLogin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User user = userService.findUserByLogin(currentUserName);
        if (user != null) {
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/admin_dashboard";
            } else if ("USER".equals(user.getRole())) {
                return "redirect:/user/user_dashboard";
            }
        }
        return "redirect:/";
    }
}
