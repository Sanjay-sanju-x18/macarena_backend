package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.SizeOptionRequest;
import com.example.macarena_backend.dto.SizeOptionResponse;
import com.example.macarena_backend.entity.SizeOption;
import com.example.macarena_backend.repository.SizeOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeOptionService {

    private final SizeOptionRepository repository;

    public SizeOptionService(SizeOptionRepository repository) {
        this.repository = repository;
    }

    public List<SizeOptionResponse> getAll() {
        return repository.findAllByOrderBySizeTypeAscDisplayOrderAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SizeOptionResponse> getByType(String sizeType) {
        return repository.findBySizeTypeOrderByDisplayOrderAsc(sizeType)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SizeOptionResponse add(SizeOptionRequest req) {
        String type = req.getSizeType().trim().toLowerCase();
        String label = req.getLabel().trim();

        if (repository.existsBySizeTypeAndLabelIgnoreCase(type, label)) {
            throw new IllegalArgumentException("Size already exists");
        }

        Integer order = req.getDisplayOrder() != null ? req.getDisplayOrder() : 0;
        SizeOption saved = repository.save(new SizeOption(type, label, order));
        return toDto(saved);
    }

    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Size not found");
        }
        repository.deleteById(id);
    }

    private SizeOptionResponse toDto(SizeOption s) {
        return new SizeOptionResponse(
                s.getId(),
                s.getSizeType(),
                s.getLabel(),
                s.getDisplayOrder()
        );
    }
}