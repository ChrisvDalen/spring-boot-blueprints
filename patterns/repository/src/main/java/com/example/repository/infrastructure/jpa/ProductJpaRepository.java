package com.example.repository.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
    List<ProductJpaEntity> findByCategory(String category);

    @Query("SELECT p FROM ProductJpaEntity p WHERE p.stock <= :threshold")
    List<ProductJpaEntity> findByStockLessThanEqual(int threshold);
}
