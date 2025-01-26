package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.CriminalRepository;
import ehu.java.springdemo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CriminalRepository criminalRepository;

    @Autowired
    private RequestService requestService;

    @GetMapping("/user_dashboard")
    public String userDashboard(Model model) {
        return "user/user_dashboard";
    }

    @GetMapping("/create_request")
    public String createRequestForm(Model model) {
        model.addAttribute("request", new Request());
        return "user/request-form";
    }

    @PostMapping("/save_request")
    public String saveRequest( @ModelAttribute Request request) {
        request.setStatus(Request.RequestStatus.PENDING);
        requestService.saveRequest(request);
        return "redirect:/user/my_requests";
    }

    @GetMapping("/my_requests")
    public String viewMyRequests(Model model, @AuthenticationPrincipal User currentUser) {
        List<Request> requests = requestService.findByUserId(currentUser.getId());
        model.addAttribute("requests", requests);
        return "user/my_requests";
    }


    @GetMapping("/criminals")
    public String viewCriminals(Model model) {
        List<Criminal> criminals = criminalRepository.findAll();
        model.addAttribute("criminals", criminals);
        return "user/criminals";
    }
}
