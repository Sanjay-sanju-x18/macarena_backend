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
    private final SoldOutProductService soldOutService;

    public ProductService(ProductRepository repository,
                          DressTypeRepository dressTypeRepo,
                          FileStorageService fileStorage,
                          SoldOutProductService soldOutService) {
        this.repository = repository;
        this.dressTypeRepo = dressTypeRepo;
        this.fileStorage = fileStorage;
        this.soldOutService = soldOutService;
    }

    // ============================================================
    // 👇 STEP 3-A — Only live products (archived filtered out)
    // ============================================================
    public List<ProductResponse> getAll() {
        return repository.findByArchivedAtIsNullOrderByIdDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(ProductRequest req, List<MultipartFile> photos) {

        DressType dressType = dressTypeRepo.findById(req.getDressTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Dress type not found"));

        Product p = new Product();
        p.setDressName(req.getDressName());
        p.setDressType(dressType);
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

    // ============================================================
    // 👇 STEP 3-B — Throw if product is archived
    // ============================================================
    public ProductResponse getById(Long id) {
        Product p = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (p.getArchivedAt() != null) {
            throw new IllegalArgumentException("Product no longer available");
        }
        return toResponse(p);
    }

    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req,
                                  List<MultipartFile> photos,
                                  List<String> removedPhotos) {

        Product p = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        DressType dressType = dressTypeRepo.findById(req.getDressTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Dress type not found"));

        p.setDressName(req.getDressName());
        p.setDressType(dressType);
        p.setPrice(req.getPrice());
        p.setOfferPercentage(req.getOfferPercentage());
        p.setOfferPrice(req.getOfferPrice());
        p.setSizeType(req.getSizeType());
        p.setTotalQty(req.getTotalQty());

        // sizes: existing list-a clear panni pudhusa add pannurom
        List<ProductSize> newSizes = req.getSizes().stream()
                .map(s -> new ProductSize(s.getSize(), s.getQty()))
                .collect(Collectors.toList());
        p.getSizes().clear();
        p.getSizes().addAll(newSizes);

        // ---------- photos ----------
        List<String> urls = new ArrayList<>(p.getPhotos());
        List<String> toDeleteFromDisk = new ArrayList<>();

        System.out.println("[update] DB photos     : " + urls);
        System.out.println("[update] removedPhotos : " + removedPhotos);

        // 1) remove selected photos
        if (removedPhotos != null) {
            for (String removed : removedPhotos) {
                String removedName = fileName(removed);
                String match = urls.stream()
                        .filter(u -> fileName(u).equals(removedName))
                        .findFirst()
                        .orElse(null);
                if (match != null) {
                    urls.remove(match);
                    toDeleteFromDisk.add(match);
                }
            }
        }

        // 2) add new photos
        if (photos != null) {
            for (MultipartFile f : photos) {
                if (!f.isEmpty()) {
                    urls.add(fileStorage.store(f));
                }
            }
        }

        // 3) at least one photo
        if (urls.isEmpty()) {
            throw new IllegalStateException("At least one photo is required");
        }

        p.getPhotos().clear();
        p.getPhotos().addAll(urls);

        Product saved = repository.save(p);

        toDeleteFromDisk.forEach(fileStorage::delete);

        return toResponse(saved);
    }

    private String fileName(String path) {
        if (path == null) return "";
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setDressName(p.getDressName());
        r.setDressTypeId(p.getDressType().getId());
        r.setDressTypeName(p.getDressType().getName());
        r.setPrice(p.getPrice());
        r.setOfferPercentage(p.getOfferPercentage());
        r.setOfferPrice(p.getOfferPrice());
        r.setSizeType(p.getSizeType());
        r.setTotalQty(p.getTotalQty());
        r.setSizes(p.getSizes().stream()
                .map(s -> new ProductResponse.SizeQtyDto(s.getSize(), s.getQty()))
                .collect(Collectors.toList()));
        r.setPhotoUrls(new ArrayList<>(p.getPhotos()));

        try {
            r.setSoldOutSizes(
                new ArrayList<>(soldOutService.getSoldOutSizes(p.getId()))
            );
        } catch (Exception e) {
            r.setSoldOutSizes(new ArrayList<>());
        }

        return r;
    }
}