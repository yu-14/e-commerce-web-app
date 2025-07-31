package com.ecommerce.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products associated with a given department ID.
     * Spring Data JPA automatically creates the query based on the method name.
     * @param departmentId The ID of the department.
     * @return A list of products.
     */
    List<Product> findByDepartmentId(Long departmentId);
}
