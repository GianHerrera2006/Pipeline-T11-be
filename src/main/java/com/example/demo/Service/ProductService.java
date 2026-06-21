package com.example.demo.Service;

import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.BrandRepository;
import com.example.demo.dto.ProductRegistrationDto;
import com.example.demo.model.Product;
import com.example.demo.model.Category;
import com.example.demo.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public Product registerProduct(ProductRegistrationDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setActiveIngredient(dto.getActiveIngredient());
        product.setPresentation(dto.getPresentation());
        product.setBasePrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getProductCode());
        product.setMetadata(dto.getMetadata());
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
        category.ifPresent(product::setCategory);

        Optional<Brand> brand = brandRepository.findById(dto.getBrandId());
        brand.ifPresent(product::setBrand);

        product.setDeletedAt(null);
        product.setRestoredAt(null);

        return productRepository.save(product);
    }

    public List<Product> findAllActive() {
        return productRepository.findAllActive();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRegistrationDto dto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = optionalProduct.get();
        product.setName(dto.getName());
        product.setActiveIngredient(dto.getActiveIngredient());
        product.setPresentation(dto.getPresentation());
        product.setBasePrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getProductCode());
        product.setMetadata(dto.getMetadata());

        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
        category.ifPresent(product::setCategory);

        Optional<Brand> brand = brandRepository.findById(dto.getBrandId());
        brand.ifPresent(product::setBrand);

        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteLogical(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = optionalProduct.get();
        product.setIsActive(false);
        product.setDeletedAt(java.time.LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional
    public void restore(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = optionalProduct.get();
        product.setIsActive(true);
        product.setDeletedAt(null);
        product.setRestoredAt(java.time.LocalDateTime.now());
        productRepository.save(product);
    }
}