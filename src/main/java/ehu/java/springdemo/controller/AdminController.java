package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static ehu.java.springdemo.constant.PageNameConstant.*;
import static ehu.java.springdemo.constant.AttrbiuteNameConstant.*;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin")
@Secured("ADMIN")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin_dashboard")
    public String adminDashboard(Model model) {
        return ADMIN_DASHBOARD_PAGE;
    }

    @GetMapping("/logout")
    public String logout() {
        return REDIRECT_LOGOUT;
    }

    @Autowired
    private CriminalService criminalService;

    @Autowired
    private RequestService requestService;

    @GetMapping("/criminals")
    public String viewCriminals(Model model) {
        model.addAttribute(CRIMINALS, criminalService.findAllCriminals());
        return ADMIN_CRIMINALS_PAGE;
    }

    @GetMapping("/criminals/new")
    public String newCriminalForm(Model model) {
        model.addAttribute(CRIMINAL, new Criminal());
        return ADMIN_CRIMINAL_FORM_PAGE;
    }

    @PostMapping("/criminals/add")
    public String addCriminal(@ModelAttribute Criminal criminal, @RequestParam("photoFile") MultipartFile photoFile) {
        criminalService.createCriminal(criminal, photoFile);
        return REDIRECT_ADMIN_CRIMINALS;
    }

    @GetMapping("/criminals/edit/{id}")
    public String editCriminalForm(@PathVariable Long id, Model model) {
        Optional<Criminal> criminalOptional = criminalService.findCriminalById(id);
        model.addAttribute(CRIMINAL, criminalOptional.get());
        return ADMIN_CRIMINAL_FORM_PAGE;
    }

    @PostMapping("/criminals/update/{id}")
    public String updateCriminal(@PathVariable Long id, @ModelAttribute Criminal criminal, @RequestParam("photoFile") MultipartFile photoFile) {
        criminalService.updateCriminal(id, criminal, photoFile);
        return REDIRECT_CRIMINAL_UPDATED_SUCCESSFULLY;
    }

    @PostMapping("/criminals/delete/{id}")
    public String deleteCriminal(@PathVariable Long id) {
        criminalService.deleteCriminalById(id);
        return REDIRECT_ADMIN_CRIMINALS;
    }

    @GetMapping("/requests")
    public String viewAllRequests(Model model) {
        model.addAttribute(REQUESTS, requestService.findAllRequests());
        return ADMIN_REQUESTS_PAGE;
    }

    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id) {
        criminalService.createCriminalFromRequest(id);
        return REDIRECT_ADMIN_REQUESTS;
    }

    @PostMapping("/requests/{id}/decline")
    public String declineRequest(@PathVariable Long id) {
        requestService.updateRequestStatus(id, Request.RequestStatus.DECLINED);
        return REDIRECT_ADMIN_REQUESTS;
    }

    @GetMapping("/criminals/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCriminalPhoto(@PathVariable Long id) {
        return criminalService.getCriminalPhotoResponse(id);
    }

    @GetMapping("/requests/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getRequestPhoto(@PathVariable Long id) {
        return requestService.getRequestPhotoResponse(id);
    }
}
