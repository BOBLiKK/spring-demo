package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.repository.CriminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CriminalService {

    @Autowired
    private CriminalRepository criminalRepository;

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
}
