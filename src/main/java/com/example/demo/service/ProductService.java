package com.example.demo.service;

import com.example.demo.ProductRequest;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${spring.redis.ttl}")
    private String ttl;

    public Product createProduct(ProductRequest productReq) {
        Product product = new Product(productReq.getName(), productReq.getDescription(), productReq.getPrice());
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Integer id) {
        String redisToken = "PRODUCT-getProductById-" + id;
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String cachedProductJson = valueOps.get(redisToken);

        ObjectMapper objectMapper = new ObjectMapper();
        if (cachedProductJson != null) {
            try {
                Map<String, Object> cachedMap = objectMapper.readValue(cachedProductJson, Map.class);
                Product product = objectMapper.convertValue(cachedMap.get("product"), Product.class);
                return Optional.of(product);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        Optional<Product> product = productRepository.findById(id.longValue());
        if (product.isPresent()) {
            Map<String, Object> mapToCache = new HashMap<>();
            mapToCache.put("product", product.get());
            mapToCache.put("success", true);
            mapToCache.put("message", "Get product by ID success");
            try {
                String productJson = objectMapper.writeValueAsString(mapToCache);
                valueOps.set(redisToken, productJson, Long.parseLong(ttl), TimeUnit.MINUTES);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return product;
    }

    public List<Product> listProducts() {
        String redisToken = "PRODUCT-listProducts";
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();

        // Try to retrieve the cached product list from Redis
        String cachedProductsJson = valueOps.get(redisToken);

        if (cachedProductsJson != null) {
            try {
                Map<String, Object> cachedMap = objectMapper.readValue(cachedProductsJson, new TypeReference<Map<String, Object>>() {});
                List<Product> products = objectMapper.convertValue(cachedMap.get("products"), new TypeReference<List<Product>>() {});
                return products;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Fetch products from the database if not found in the cache
        List<Product> products = productRepository.findAll();
        Map<String, Object> mapToCache = new HashMap<>();
        mapToCache.put("products", products);
        mapToCache.put("success", true);
        mapToCache.put("message", "Get all list product success");

        try {
            String productsJson = objectMapper.writeValueAsString(mapToCache);
            valueOps.set(redisToken, productsJson, Long.parseLong(ttl), TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return products;
    }

    public Product updateProduct(ProductRequest productReq) {
        Product product = new Product(productReq.getId(), productReq.getName(), productReq.getDescription(), productReq.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        String redisToken = "PRODUCT-listProducts";
        String redisTokenProductById = "PRODUCT-getProductById-" + id;

        redisTemplate.delete(redisToken);
        redisTemplate.delete(redisTokenProductById);
        productRepository.deleteById(id.longValue());
    }
}
