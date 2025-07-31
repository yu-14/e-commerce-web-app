package com.ecommerce.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "csv_id", nullable = false)
    private Integer csvId;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "name", length = 1024)
    private String name;

    @Column(name = "brand", length = 255)
    private String brand;

    @Column(name = "retail_price", precision = 10, scale = 2)
    private BigDecimal retailPrice;

    // The 'department' String is now replaced with a relationship
    // to the Department entity.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY is efficient
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "distribution_center_id")
    private Integer distributionCenterId;

    // Constructors
    public Product() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCsvId() {
        return csvId;
    }

    public void setCsvId(Integer csvId) {
        this.csvId = csvId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getDistributionCenterId() {
        return distributionCenterId;
    }

    public void setDistributionCenterId(Integer distributionCenterId) {
        this.distributionCenterId = distributionCenterId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
