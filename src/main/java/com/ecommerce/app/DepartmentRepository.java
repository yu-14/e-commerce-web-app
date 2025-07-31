package com.ecommerce.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Finds a department by its name.
     * Spring Data JPA will automatically implement this method based on its name.
     * @param name The name of the department to find.
     * @return An Optional containing the department if found.
     */
    Optional<Department> findByName(String name);
}
