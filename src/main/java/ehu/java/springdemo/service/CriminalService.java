package ehu.java.springdemo.service;

import ehu.java.springdemo.controller.AdminController;
import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.repository.CriminalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CriminalService {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CriminalRepository criminalRepository;

    @Autowired
    private RequestService requestService;

    public List<Criminal> findAllCriminals() {
        return criminalRepository.findAll();
    }

    public Optional<Criminal> findCriminalById(Long id) {
        return criminalRepository.findById(id);
    }

    public void saveCriminal(Criminal criminal) {
        criminalRepository.save(criminal);
    }

    public void deleteCriminalById(Long id) {
        criminalRepository.deleteById(id);
    }

    public ResponseEntity<byte[]> getCriminalPhotoResponse(Long id) {
        Optional<Criminal> criminal = findCriminalById(id);
        return criminal.map(c -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/*")
                        .body(c.getPhoto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    public void createCriminal(Criminal criminal, MultipartFile photoFile) {
        try {
            if (!photoFile.isEmpty()) {
                criminal.setPhoto(photoFile.getBytes());
            }
            criminalRepository.save(criminal);
        } catch (IOException e) {
            logger.error("Error saving criminal. " + e.getMessage());
        }
    }

    public void updateCriminal(Long id, Criminal criminal, MultipartFile photoFile) {
        Optional<Criminal> criminalOptional = findCriminalById(id);
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
                logger.error(e.getMessage());
            }
        }
        saveCriminal(existingCriminal);
    }

    public void createCriminalFromRequest(Long id){
        Optional<Request> requestOptional = requestService.findRequestById(id);
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
        saveCriminal(criminal);
        requestService.updateRequestStatus(id, Request.RequestStatus.APPROVED);
    }
}
