package com.ecommerce.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository, ProductRepository productRepository) {
        this.departmentRepository = departmentRepository;
        this.productRepository = productRepository;
    }

    /**
     * Endpoint to list all departments with their product counts.
     * Example URL: /api/departments
     * @return A list of departments with names and product counts.
     */
    @GetMapping
    public ResponseEntity<Map<String, List<DepartmentDTO>>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentRepository.findAllWithProductCount();
        return ResponseEntity.ok(Map.of("departments", departments));
    }

    /**
     * Endpoint to get a specific department by its ID.
     * Example URL: /api/departments/1
     * @param id The ID of the department.
     * @return The department details if found, otherwise 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with id: " + id));
        return ResponseEntity.ok(department);
    }

    /**
     * Endpoint to get all products within a specific department.
     * Example URL: /api/departments/1/products
     * @param id The ID of the department.
     * @return A list of products in that department.
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<Map<String, Object>> getProductsByDepartment(@PathVariable Long id) {
        // First, check if the department exists
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found with id: " + id));
        
        List<Product> products = productRepository.findByDepartmentId(id);
        
        // Create the custom JSON response structure
        Map<String, Object> response = Map.of(
            "department", department.getName(),
            "products", products
        );

        return ResponseEntity.ok(response);
    }
}
