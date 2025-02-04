package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    public Optional<Request> findRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public List<Request> findByUserId(Long userId) {
        List<Object[]> results = requestRepository.findRequestSummaryByUserId(userId);
        List<Request> requests = new ArrayList<>();
        for (Object[] row : results) {
            Request request = new Request();
            request.setId((Long) row[0]);
            request.setComment((String) row[1]);
            request.setDateOfBirth((LocalDate) row[2]);
            request.setFirstName((String) row[3]);
            request.setLastName((String) row[4]);
            request.setNationality((String) row[5]);
            request.setReasonForWanted((String) row[6]);
            request.setReward((Double) row[7]);
            request.setStatus((Request.RequestStatus) row[8]);
            request.setUserId((Long) row[9]);

            requests.add(request);
        }
        return requests;
    }

    public List<Request> findAllRequests() {
        return requestRepository.findAll();
    }

    public void updateRequestStatus(Long id, Request.RequestStatus status) {
        Request request = requestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        requestRepository.save(request);
    }
}
