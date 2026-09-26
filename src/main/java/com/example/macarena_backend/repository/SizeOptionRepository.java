package com.example.macarena_backend.repository;

import com.example.macarena_backend.entity.SizeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeOptionRepository extends JpaRepository<SizeOption, Long> {

    List<SizeOption> findBySizeTypeOrderByDisplayOrderAsc(String sizeType);

    List<SizeOption> findAllByOrderBySizeTypeAscDisplayOrderAsc();

    Optional<SizeOption> findBySizeTypeAndLabelIgnoreCase(String sizeType, String label);

    boolean existsBySizeTypeAndLabelIgnoreCase(String sizeType, String label);
}