package com.ecommerce.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoader.class);

    private final ProductRepository productRepository;

    public CsvDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data is already loaded to prevent duplicate entries on restart
        if (productRepository.count() > 0) {
            log.info("Database already contains product data. Skipping CSV loading.");
            return;
        }

        log.info("Loading product data from CSV file...");
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("products.csv").getInputStream()))) {
            
            String line;
            boolean isHeader = true;
            List<Product> productBatch = new ArrayList<>();
            final int BATCH_SIZE = 1000; // Process records in batches for efficiency

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // CSV columns: id,cost,category,name,brand,retail_price,department,sku,distribution_center_id
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                if (data.length < 9) {
                    log.warn("Skipping malformed line: {}", line);
                    continue;
                }

                Product product = new Product();
                
                try {
                    product.setCsvId(parseInteger(data[0]));
                    product.setCost(parseBigDecimal(data[1]));
                    product.setCategory(sanitizeString(data[2]));
                    product.setName(sanitizeString(data[3]));
                    product.setBrand(sanitizeString(data[4]));
                    product.setRetailPrice(parseBigDecimal(data[5]));
                    product.setDepartment(sanitizeString(data[6]));
                    product.setSku(sanitizeString(data[7]));
                    product.setDistributionCenterId(parseInteger(data[8]));

                    productBatch.add(product);

                    // Save in batches
                    if (productBatch.size() >= BATCH_SIZE) {
                        productRepository.saveAll(productBatch);
                        productBatch.clear();
                        log.info("Saved a batch of {} products.", BATCH_SIZE);
                    }
                } catch (Exception e) {
                    log.error("Error parsing line: {}. Skipping. Error: {}", line, e.getMessage());
                }
            }
            
            // Save any remaining products in the last batch
            if (!productBatch.isEmpty()) {
                productRepository.saveAll(productBatch);
                log.info("Saved the final batch of {} products.", productBatch.size());
            }

            long endTime = System.currentTimeMillis();
            log.info("Finished loading data in {} ms.", (endTime - startTime));
            log.info("Total products loaded: {}", productRepository.count());

        } catch (Exception e) {
            log.error("Failed to load CSV data.", e);
        }
    }

    private String sanitizeString(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("NA")) {
            return null;
        }
        // Remove quotes if present at the beginning or end of the string
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
