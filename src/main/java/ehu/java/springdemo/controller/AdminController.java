package ehu.java.springdemo.controller;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin")
@Secured("ADMIN")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin_dashboard")
    public String adminDashboard(Model model) {
        return "admin/admin_dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }

    @Autowired
    private CriminalService criminalService;

    @Autowired
    private RequestService requestService;

    @GetMapping("/criminals")
    public String viewCriminals(Model model) {
        List<Criminal> criminals = criminalService.findAllCriminals();
        model.addAttribute("criminals", criminals);
        return "admin/criminals";
    }

    @GetMapping("/criminals/new")
    public String newCriminalForm(Model model) {
        model.addAttribute("criminal", new Criminal());
        return "admin/criminal-form";
    }

    @PostMapping("/criminals/add")
    public String addCriminal(@ModelAttribute Criminal criminal, @RequestParam("photoFile") MultipartFile photoFile) {
        try {
            criminal.setPhoto(photoFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error";
        }
        criminalService.saveCriminal(criminal);
        return "redirect:/admin/criminals";
    }

    @GetMapping("/criminals/edit/{id}")
    public String editCriminalForm(@PathVariable Long id, Model model) {
        Optional<Criminal> criminalOptional = criminalService.findCriminalById(id);
        if (criminalOptional.isPresent()) {
            model.addAttribute("criminal", criminalOptional.get());
            return "admin/criminal-form";
        } else {
            return "redirect:/admin/criminals?error=CriminalNotFound";
        }
    }

    @PostMapping("/criminals/update/{id}")
    public String updateCriminal(@PathVariable Long id, @ModelAttribute Criminal criminal, @RequestParam("photoFile") MultipartFile photoFile) {
        Optional<Criminal> criminalOptional = criminalService.findCriminalById(id);
        if (criminalOptional.isPresent()) {
            Criminal existingCriminal = criminalOptional.get();
            existingCriminal.setFirstName(criminal.getFirstName());
            existingCriminal.setLastName(criminal.getLastName());
            existingCriminal.setDateOfBirth(criminal.getDateOfBirth());
            existingCriminal.setNationality(criminal.getNationality());
            existingCriminal.setReasonForWanted(criminal.getReasonForWanted());
            existingCriminal.setReward(criminal.getReward());
            existingCriminal.setComment(criminal.getComment());

            if (photoFile != null && !photoFile.isEmpty()) {
                try {
                    existingCriminal.setPhoto(photoFile.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/admin/criminals?error=PhotoUploadFailed";
                }
            }
            criminalService.saveCriminal(existingCriminal);
            return "redirect:/admin/criminals?success=CriminalUpdated";
        } else {
            return "redirect:/admin/criminals?error=CriminalNotFound";
        }
    }

    @PostMapping("/criminals/delete/{id}")
    public String deleteCriminal(@PathVariable Long id) {
        criminalService.deleteCriminalById(id);
        return "redirect:/admin/criminals";
    }



    @GetMapping("/requests")
    public String viewAllRequests(Model model) {
        List<Request> requests = requestService.findAllRequests();
        model.addAttribute("requests", requests);
        return "admin/requests";
    }


    @PostMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id) {
        Optional<Request> requestOptional = requestService.findRequestById(id);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            Criminal criminal = new Criminal();
            criminal.setFirstName(request.getFirstName());
            criminal.setLastName(request.getLastName());
            criminal.setDateOfBirth(request.getDateOfBirth());
            criminal.setNationality(request.getNationality());
            criminal.setReasonForWanted(request.getReasonForWanted());
            criminal.setReward(request.getReward());
            criminal.setPhoto(request.getPhoto());
            criminal.setComment(request.getComment());

            criminalService.saveCriminal(criminal);
            requestService.updateRequestStatus(id, Request.RequestStatus.APPROVED);
        }

        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/decline")
    public String declineRequest(@PathVariable Long id) {
        requestService.updateRequestStatus(id, Request.RequestStatus.DECLINED);
        return "redirect:/admin/requests";
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

    @GetMapping("/requests/photo/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getRequestPhoto(@PathVariable Long id) {
        Optional<Request> request = requestService.findRequestById(id);
        if (request.isPresent() && request.get().getPhoto() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/*")
                    .body(request.get().getPhoto());
        }
        return ResponseEntity.notFound().build();
    }
}
