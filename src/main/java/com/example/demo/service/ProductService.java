package com.example.demo.service;

import com.example.demo.ProductRequest;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(ProductRequest productReq) {
        Product product = new Product(productReq.getName(), productReq.getDescription(), productReq.getPrice());
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id.longValue());
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(ProductRequest productReq) {
        Product product = new Product(productReq.getId(), productReq.getName(), productReq.getDescription(), productReq.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id.longValue());
    }
}
