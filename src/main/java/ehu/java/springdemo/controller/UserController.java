package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.CriminalRepository;
import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CriminalRepository criminalRepository;

    @Autowired
    private CriminalService criminalService;

    @Autowired
    private RequestService requestService;


    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }


    @GetMapping("/user_dashboard")
    public String userDashboard(Model model) {
        return "user/user_dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }

    @GetMapping("/create_request")
    public String createRequestForm(Model model) {
        model.addAttribute("request", new Request());
        return "user/request-form";
    }

    @PostMapping("/save_request")
    public String saveRequest(@ModelAttribute Request request, HttpSession session, @RequestParam("photoFile") MultipartFile photoFile) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            request.setPhoto(photoFile.getBytes());
            request.setUserId(currentUser.getId());
            request.setStatus(Request.RequestStatus.PENDING);
            requestService.saveRequest(request);
            return "redirect:/user/user_dashboard";
        } catch (Exception e) {
            return "redirect:/user/create_request?error";
        }
    }

    @GetMapping("/my_requests")
    public String viewMyRequests(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        List<Request> requests = requestService.findByUserId(currentUser.getId());
        model.addAttribute("requests", requests != null ? requests : Collections.emptyList());
        return "user/my_requests";
    }

    @GetMapping("/criminals")
    public String viewCriminals(Model model) {
        List<Criminal> criminals = criminalRepository.findAll();
        model.addAttribute("criminals", criminals);
        return "user/criminals";
    }

    @GetMapping("/criminals/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCriminalPhoto(@PathVariable Long id) {
        Optional<Criminal> criminal = criminalService.findCriminalById(id);

        if (criminal.isPresent() && criminal.get().getPhoto() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/*")
                    .body(criminal.get().getPhoto());
        }
        return ResponseEntity.notFound().build();
    }
    }
