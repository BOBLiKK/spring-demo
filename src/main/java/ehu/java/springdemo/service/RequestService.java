package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> findByUserId(Long userId) {
        return requestRepository.findByUserId(userId); // Обновлен вызов метода
    }

    public List<Request> findApprovedRequests() {
        return requestRepository.findByStatus(Request.RequestStatus.APPROVED);
    }

    public void saveRequest(Request request) {
        requestRepository.save(request);
    }
}
