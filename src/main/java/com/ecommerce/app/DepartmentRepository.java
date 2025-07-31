package com.ecommerce.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    /**
     * Custom query to fetch all departments along with the count of products in each department.
     * It uses a LEFT JOIN to include departments that may have zero products.
     * The result is projected into the DepartmentDTO.
     * @return A list of DepartmentDTOs.
     */
    @Query("SELECT new com.ecommerce.app.DepartmentDTO(d.id, d.name, COUNT(p.id)) " +
           "FROM Department d LEFT JOIN Product p ON d.id = p.department.id " +
           "GROUP BY d.id, d.name ORDER BY d.name")
    List<DepartmentDTO> findAllWithProductCount();
}
