package com.example.macarena_backend.service;

import com.example.macarena_backend.entity.Courier;
import com.example.macarena_backend.repository.CourierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierService {

    private final CourierRepository repo;

    public CourierService(CourierRepository repo) {
        this.repo = repo;
    }

    public List<Courier> getAll() {
        return repo.findAll();
    }

    public Courier create(Courier c) {
        String name = c.getCompanyName().trim();
        if (repo.existsByCompanyNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Courier company already exists");
        }
        c.setId(null);
        c.setCompanyName(name);
        return repo.save(c);
    }

    public Courier update(Long id, Courier data) {
        Courier existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        String name = data.getCompanyName().trim();
        if (repo.existsByCompanyNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException("Courier company already exists");
        }
        existing.setCompanyName(name);
        existing.setPhoneNumber(data.getPhoneNumber());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Courier not found");
        }
        repo.deleteById(id);
    }
}