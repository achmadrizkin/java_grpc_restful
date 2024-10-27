package com.example.demo.controller.grpc;

import com.example.demo.*;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
public class ProductController extends ProductServiceGrpc.ProductServiceImplBase {

    @Autowired
    private ProductService productService;

    @Override
    public void createProduct(ProductRequest productReq, StreamObserver<ProductResponse> productResponseObserver) {
        System.out.println("--- created product executed ---");

        // Create a new Product without setting the ID, as it will be auto-generated
        Product createdProduct = productService.createProduct(productReq);

        // Build the ProductRequest from createdProduct to reflect any auto-generated fields
        ProductRequest createdProductRequest = ProductRequest.newBuilder()
                .setId(productReq.getId()) // Use the auto-generated ID from createdProduct
                .setName(productReq.getName())
                .setDescription(productReq.getDescription())
                .setPrice(productReq.getPrice())
                .build();

        // Build the response using the createdProductRequest
        ProductResponse productResponse = ProductResponse.newBuilder()
                .setProduct(createdProductRequest)
                .setMessage("Created product success")
                .setSuccess(true)
                .build();

        // Send the response and mark the process as completed
        productResponseObserver.onNext(productResponse);
        productResponseObserver.onCompleted();
    }

    @Override
    public void getProduct(ProductId productId, StreamObserver<ProductResponse> productResponseObserver) {
        System.out.println("--- get product by ID: " + productId.getId() + " ---");

        // Attempt to retrieve the product by ID
        Optional<Product> productOpt = productService.getProductById(productId.getId());

        // Check if the product is available
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Build the ProductRequest from the retrieved product
            ProductRequest prod = ProductRequest.newBuilder()
                    .setId(product.getId().intValue())  // Assuming id is of type Long; convert to int if necessary
                    .setName(product.getName())
                    .setDescription(product.getDescription())
                    .setPrice(product.getPrice().doubleValue())  // Assuming price is BigDecimal; convert to double if necessary
                    .build();

            // Build the successful ProductResponse
            ProductResponse productResponse = ProductResponse.newBuilder()
                    .setProduct(prod)
                    .setMessage("Get product success")
                    .setSuccess(true)
                    .build();

            // Send the successful response
            productResponseObserver.onNext(productResponse);
        } else {
            // Build an error response if the product is not found
            ProductResponse productResponse = ProductResponse.newBuilder()
                    .setMessage("Product not found")
                    .setSuccess(false)
                    .build();

            // Send the error response
            productResponseObserver.onNext(productResponse);
        }

        // Mark the process as completed
        productResponseObserver.onCompleted();
    }

    @Override
    public void listProducts(ProductEmpty productEmpty, StreamObserver<ProductListResponse> productListResponseObserver) {
        System.out.println("--- get list product ---");

        // Retrieve the list of products from the product service
        List<Product> listProducts = productService.listProducts();
        System.out.println("listProducts: "+ listProducts);

        // Convert each Product entity to a ProductRequest and collect them into a list
        List<ProductRequest> products = listProducts.stream()
                .map(product -> ProductRequest.newBuilder()
                        .setId(product.getId().intValue())  // Assuming id is of type Long; convert to int if necessary
                        .setName(product.getName())
                        .setDescription(product.getDescription())
                        .setPrice(product.getPrice().doubleValue())  // Assuming price is BigDecimal; convert to double if necessary
                        .build())
                .collect(Collectors.toList());

        // Build the ProductListResponse with the product list
        ProductListResponse productListResponse = ProductListResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Get all list product success")
                .addAllProducts(products)  // Use addAllProducts to add the list of products
                .build();

        // Send the response and mark the process as completed
        productListResponseObserver.onNext(productListResponse);
        productListResponseObserver.onCompleted();
    }

    @Override
    public void updateProduct(ProductRequest productRequest, StreamObserver<ProductResponse> productResponseObserver) {
        System.out.println("--- update product by " + productRequest.getId() + " ---");

        Optional<Product> productOpt = productService.getProductById(productRequest.getId());
        if (productOpt.isPresent()) {
            Product createdProduct = productService.updateProduct(productRequest);

            ProductRequest prodReq = ProductRequest.newBuilder()
                    .setId(productRequest.getId())
                    .setName(productRequest.getName())
                    .setDescription(productRequest.getDescription())
                    .setPrice(productRequest.getPrice())
                    .build();

            // Build the successful ProductResponse
            ProductResponse productDeleteResponse = ProductResponse.newBuilder()
                    .setProduct(prodReq)
                    .setMessage("Update product success by " + productRequest.getId())
                    .setSuccess(true)
                    .build();

            // Send the successful response
            productResponseObserver.onNext(productDeleteResponse);
        } else {
            ProductResponse productDeleteResponse = ProductResponse.newBuilder()
                    .setMessage("Product id is not found")
                    .setSuccess(false)
                    .build();

            // Send the error response
            productResponseObserver.onNext(productDeleteResponse);
        }
        productResponseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(ProductId productId, StreamObserver<ProductDeleteResponse> productDeleteResponseObserver) {
        System.out.println("--- delete product by " + productId + " ---");

        // Attempt to retrieve the product by ID
        Optional<Product> productOpt = productService.getProductById(productId.getId());

        // Check if the product is available
        if (productOpt.isPresent()) {
            productService.deleteProduct(productId.getId());

            // Build the successful ProductResponse
            ProductDeleteResponse productDeleteResponse = ProductDeleteResponse.newBuilder()
                    .setMessage("Delete product success")
                    .setSuccess(true)
                    .build();

            // Send the successful response
            productDeleteResponseObserver.onNext(productDeleteResponse);
        } else {
            // Build an error response if the product is not found
            ProductDeleteResponse productResponse = ProductDeleteResponse.newBuilder()
                    .setMessage("Product not found")
                    .setSuccess(false)
                    .build();

            // Send the error response
            productDeleteResponseObserver.onNext(productResponse);
        }
        productDeleteResponseObserver.onCompleted();
    }
}
