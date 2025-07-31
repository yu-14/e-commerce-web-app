package com.ecommerce.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Data JPA will automatically implement the basic CRUD operations.
    // You can define custom query methods here in the future if needed.
    // For example:
    // Optional<Product> findBySku(String sku);
}
