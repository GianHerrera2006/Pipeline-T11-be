package com.example.demo.Service;

import com.example.demo.Repository.CategoryRepository;
import com.example.demo.dto.CategoryRegistrationDto;
import com.example.demo.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Category registerCategory(CategoryRegistrationDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setLevel(dto.getLevel() != null ? dto.getLevel() : 0);
        category.setCode(dto.getCode());
        category.setOrderDisplay(dto.getOrderDisplay() != null ? dto.getOrderDisplay() : 0);
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getParentCategoryId() != null) {
            Optional<Category> parent = categoryRepository.findById(dto.getParentCategoryId());
            parent.ifPresent(category::setParentCategory);
        }

        category.setDeletedAt(null);
        category.setRestoredAt(null);

        return categoryRepository.save(category);
    }

    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryRegistrationDto dto) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Category category = optionalCategory.get();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        if (dto.getLevel() != null) {
            category.setLevel(dto.getLevel());
        }
        category.setCode(dto.getCode());
        if (dto.getOrderDisplay() != null) {
            category.setOrderDisplay(dto.getOrderDisplay());
        }
        if (dto.getParentCategoryId() != null) {
            Optional<Category> parent = categoryRepository.findById(dto.getParentCategoryId());
            parent.ifPresent(category::setParentCategory);
        }
        if (dto.getIsActive() != null) {
            category.setIsActive(dto.getIsActive());
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteLogical(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Category category = optionalCategory.get();
        category.setIsActive(false);
        category.setDeletedAt(java.time.LocalDateTime.now());
        categoryRepository.save(category);
    }

    @Transactional
    public void restore(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Category category = optionalCategory.get();
        category.setIsActive(true);
        category.setDeletedAt(null);
        category.setRestoredAt(java.time.LocalDateTime.now());
        categoryRepository.save(category);
    }
}