package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.ProductRequest;
import com.example.macarena_backend.dto.ProductResponse;
import com.example.macarena_backend.entity.DressType;
import com.example.macarena_backend.entity.Product;
import com.example.macarena_backend.entity.ProductSize;
import com.example.macarena_backend.repository.DressTypeRepository;
import com.example.macarena_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final DressTypeRepository dressTypeRepo;
    private final FileStorageService fileStorage;

    public ProductService(ProductRepository repository,
                          DressTypeRepository dressTypeRepo,
                          FileStorageService fileStorage) {
        this.repository = repository;
        this.dressTypeRepo = dressTypeRepo;
        this.fileStorage = fileStorage;
    }

    public List<ProductResponse> getAll() {
        return repository.findAllByOrderByIdDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(ProductRequest req, List<MultipartFile> photos) {

        // ✅ Look up the DressType by ID from the existing table
        DressType dressType = dressTypeRepo.findById(req.getDressTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Dress type not found"));

        Product p = new Product();
        p.setDressName(req.getDressName());
        p.setDressType(dressType);                 // ✅ set FK
        p.setPrice(req.getPrice());
        p.setOfferPercentage(req.getOfferPercentage());
        p.setOfferPrice(req.getOfferPrice());
        p.setSizeType(req.getSizeType());
        p.setTotalQty(req.getTotalQty());

        List<ProductSize> sizes = req.getSizes().stream()
                .map(s -> new ProductSize(s.getSize(), s.getQty()))
                .collect(Collectors.toList());
        p.setSizes(sizes);

        List<String> urls = new ArrayList<>();
        if (photos != null) {
            for (MultipartFile f : photos) {
                if (!f.isEmpty()) {
                    urls.add(fileStorage.store(f));
                }
            }
        }
        p.setPhotos(urls);

        return toResponse(repository.save(p));
    }

    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        repository.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setDressName(p.getDressName());
        r.setDressTypeId(p.getDressType().getId());        // ✅
        r.setDressTypeName(p.getDressType().getName());    // ✅
        r.setPrice(p.getPrice());
        r.setOfferPercentage(p.getOfferPercentage());
        r.setOfferPrice(p.getOfferPrice());
        r.setSizeType(p.getSizeType());
        r.setTotalQty(p.getTotalQty());
        r.setSizes(p.getSizes().stream()
                .map(s -> new ProductResponse.SizeQtyDto(s.getSize(), s.getQty()))
                .collect(Collectors.toList()));
        r.setPhotoUrls(p.getPhotos());
        return r;
    }
}