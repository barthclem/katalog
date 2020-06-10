package com.klem.catalog.controllers;

import com.klem.catalog.controllers.common.Response;
import com.klem.catalog.exceptions.ResourceNotFoundException;
import com.klem.catalog.models.Category;
import com.klem.catalog.repositories.VehicleCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@RestController
@RequestMapping("category")
@Validated
public class CategoryController {

    private final VehicleCategoryRepository categoryRepository;

    public CategoryController(VehicleCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "limit", defaultValue = "20") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Category> cats = categoryRepository.findAll(pageRequest);
        return ResponseEntity.ok(new Response<>(true, "fetch all categories", cats.getContent()));
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        Category cat = categoryRepository.save(category);

        return new ResponseEntity<>(new Response<>(true, "Added a new category", cat), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@NotBlank @RequestParam String categoryId, @Valid @RequestBody Category category) {
        Category updatedCat = categoryRepository.findById(UUID.fromString(categoryId))
                .map(cat -> {
                    cat.setName(category.getName());
                    return categoryRepository.save(cat);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Could not update, Category Not Found."));

        return ResponseEntity.ok(new Response<>(true, "Updated category", updatedCat));
    }


}
