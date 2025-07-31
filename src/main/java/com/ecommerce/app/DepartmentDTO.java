package com.ecommerce.app;

// A Data Transfer Object (DTO) to create a custom JSON response.
public class DepartmentDTO {

    private Long id;
    private String name;
    private long productCount;

    public DepartmentDTO(Long id, String name, long productCount) {
        this.id = id;
        this.name = name;
        this.productCount = productCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
}
