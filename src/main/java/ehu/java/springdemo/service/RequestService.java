package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    public List<Request> findAllRequests(){
        return requestRepository.findAll();
    }

    public Optional<Request> findRequestById(Long id) {
        return requestRepository.findById(id);
    }
}
