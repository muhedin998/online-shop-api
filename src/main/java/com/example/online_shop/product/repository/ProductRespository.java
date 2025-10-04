package com.example.online_shop.product.repository;

import com.example.online_shop.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRespository extends JpaRepository<Product, Long> {
    List<Product> findByFeaturedTrue();

    Page<Product> findByFeaturedFalse(Pageable pageable);
}
