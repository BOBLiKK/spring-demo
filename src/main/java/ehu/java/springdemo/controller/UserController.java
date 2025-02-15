package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.event.UserLogoutEvent;
import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import static ehu.java.springdemo.constant.AttrbiuteNameConstant.*;
import static ehu.java.springdemo.constant.PageNameConstant.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CriminalService criminalService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(CURRENT_USER);
    }

    @GetMapping("/user_dashboard")
    public String userDashboard(Model model) {
        return USER_DASHBOARD_PAGE;
    }

    @GetMapping("/logout")
    public String logout(Authentication authentication) {
        eventPublisher.publishEvent(new UserLogoutEvent(this, authentication.getName()));
        return REDIRECT_LOGOUT;
    }

    @GetMapping("/create_request")
    public String createRequestForm(Model model) {
        model.addAttribute(REQUEST, new Request());
        return USER_REQUEST_FORM_PAGE;
    }

    @PostMapping("/save_request")
    public String saveRequest(@ModelAttribute Request request, HttpSession session, @RequestParam("photoFile") MultipartFile photoFile) {
        User currentUser = (User) session.getAttribute(CURRENT_USER);
        if (currentUser == null) {
            return REDIRECT_LOGIN;
        }
        try {
            requestService.createRequest(request, currentUser.getId(), photoFile);
            return REDIRECT_USER_DASHBOARD;
        } catch (Exception e) {
            return REDIRECT_SAVE_USER_ERROR;
        }
    }

    @GetMapping("/my_requests")
    public String viewMyRequests(Model model, HttpSession session) {
        model.addAttribute(REQUESTS, requestService.getUserRequests(session));
        return USER_MY_REQUESTS_PAGE;
    }

    @GetMapping("/criminals")
    public String viewCriminals(Model model) {
        List<Criminal> criminals = criminalService.findAllCriminals();
        model.addAttribute(CRIMINALS, criminals);
        return USER_CRIMINALS_PAGE;
    }

    @GetMapping("/criminals/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCriminalPhoto(@PathVariable Long id) {
        return criminalService.getCriminalPhotoResponse(id);
    }
}
