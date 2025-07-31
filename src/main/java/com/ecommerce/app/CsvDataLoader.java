package com.ecommerce.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);

    private final ProductRepository productRepository;
    private final DepartmentRepository departmentRepository;

    // Inject both repositories
    public CsvDataLoader(ProductRepository productRepository, DepartmentRepository departmentRepository) {
        this.productRepository = productRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data is already loaded
        if (productRepository.count() > 0 || departmentRepository.count() > 0) {
            log.info("Database already contains data. Skipping CSV loading.");
            return;
        }

        log.info("Loading data from CSV file...");
        long startTime = System.currentTimeMillis();

        // Use a map as a cache to store and retrieve departments efficiently
        Map<String, Department> departmentCache = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("products.csv").getInputStream()))) {

            String line;
            boolean isHeader = true;
            List<Product> productBatch = new ArrayList<>();
            final int BATCH_SIZE = 1000;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length < 9) {
                    log.warn("Skipping malformed line: {}", line);
                    continue;
                }

                try {
                    String departmentName = sanitizeString(data[6]);
                    if (departmentName == null || departmentName.isEmpty()) {
                        log.warn("Skipping product with no department: {}", line);
                        continue; // Skip products without a department
                    }

                    // Get or create the department
                    Department department = departmentCache.computeIfAbsent(departmentName, name -> {
                        return departmentRepository.findByName(name)
                                .orElseGet(() -> departmentRepository.save(new Department(name)));
                    });

                    Product product = new Product();
                    product.setCsvId(parseInteger(data[0]));
                    product.setCost(parseBigDecimal(data[1]));
                    product.setCategory(sanitizeString(data[2]));
                    product.setName(sanitizeString(data[3]));
                    product.setBrand(sanitizeString(data[4]));
                    product.setRetailPrice(parseBigDecimal(data[5]));
                    product.setSku(sanitizeString(data[7]));
                    product.setDistributionCenterId(parseInteger(data[8]));
                    
                    // Set the department object on the product
                    product.setDepartment(department);

                    productBatch.add(product);

                    if (productBatch.size() >= BATCH_SIZE) {
                        productRepository.saveAll(productBatch);
                        productBatch.clear();
                        log.info("Saved a batch of {} products.", BATCH_SIZE);
                    }
                } catch (Exception e) {
                    log.error("Error parsing line: {}. Skipping. Error: {}", line, e.getMessage());
                }
            }

            if (!productBatch.isEmpty()) {
                productRepository.saveAll(productBatch);
                log.info("Saved the final batch of {} products.", productBatch.size());
            }

            long endTime = System.currentTimeMillis();
            log.info("Finished loading data in {} ms.", (endTime - startTime));
            log.info("Total departments created: {}", departmentRepository.count());
            log.info("Total products loaded: {}", productRepository.count());

        } catch (Exception e) {
            log.error("Failed to load CSV data.", e);
        }
    }

    private String sanitizeString(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("NA")) {
            return null;
        }
        return value.trim().replaceAll("^\"|\"$", "");
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("NA")) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Could not parse integer value: '{}'. Setting to null.", value);
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("NA")) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Could not parse BigDecimal value: '{}'. Setting to null.", value);
            return null;
        }
    }
}
