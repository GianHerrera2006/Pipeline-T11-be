package com.example.demo.Repository;

import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND c.isActive = true")
    List<Category> findAllActive();

    List<Category> findByParentCategoryIsNull();

    Optional<Category> findByName(String name);
}