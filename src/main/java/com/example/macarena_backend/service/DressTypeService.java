package com.example.macarena_backend.service;

import com.example.macarena_backend.entity.DressType;
import com.example.macarena_backend.repository.DressTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DressTypeService {

    private final DressTypeRepository repository;

    public DressTypeService(DressTypeRepository repository) {
        this.repository = repository;
    }

    public List<DressType> getAll() {
        return repository.findAll();
    }

    public DressType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dress type not found"));
    }

    @Transactional
    public DressType add(String name) {
        String trimmed = name.trim();
        if (repository.existsByNameIgnoreCase(trimmed)) {
            throw new IllegalArgumentException("Dress type already exists");
        }
        return repository.save(new DressType(trimmed));
    }

    @Transactional
    public DressType update(Long id, String newName) {
        DressType type = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dress type not found"));

        String trimmed = newName.trim();

        if (!type.getName().equalsIgnoreCase(trimmed)
                && repository.existsByNameIgnoreCase(trimmed)) {
            throw new IllegalArgumentException("Dress type already exists");
        }

        type.setName(trimmed);
        return repository.save(type);
    }

    // ✅ Delete by ID (not by name)
    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Dress type not found");
        }
        repository.deleteById(id);
    }
}