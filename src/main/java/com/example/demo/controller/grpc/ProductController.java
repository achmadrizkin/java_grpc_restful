package com.example.demo.controller.grpc;

import com.example.demo.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Arrays;
import java.util.List;

@GrpcService
public class ProductController extends ProductServiceGrpc.ProductServiceImplBase {
    @Override
    public void createProduct(ProductRequest productReq, StreamObserver<ProductResponse> productResponseObserver) {
        System.out.println("--- created product executed ---");

        ProductResponse productResponse = ProductResponse.newBuilder().setProduct(productReq).setMessage("Created product success").setSuccess(true).build();

        // Send the response and mark the process as completed
        productResponseObserver.onNext(productResponse);
        productResponseObserver.onCompleted();
    }

    @Override
    public void getProduct(ProductId productId, StreamObserver<ProductResponse> productResponseObserver) {
        System.out.println("--- get product by " + productId + " ---");

        ProductRequest productResponsee = ProductRequest.newBuilder().setId(12).setName("Product A").setDescription("Test").setPrice(100).build();
        ProductResponse productResponse = ProductResponse.newBuilder().setProduct(productResponsee).setMessage("Get product success").setSuccess(true).build();

        // Send the response and mark the process as completed
        productResponseObserver.onNext(productResponse);
        productResponseObserver.onCompleted();
    }

    @Override
    public void listProducts(ProductEmpty productEmpty, StreamObserver<ProductListResponse> productListResponseObserver) {
        System.out.println("--- get list product ---");

        // Create a list of ProductRequest objects (sample products)
        List<ProductRequest> products = Arrays.asList(
                ProductRequest.newBuilder().setId(1).setName("Product 1").setDescription("Description 1").setPrice(10.0).build(),
                ProductRequest.newBuilder().setId(2).setName("Product 2").setDescription("Description 2").setPrice(20.0).build()
        );

        // Build the ProductListResponse with the product list using addAllProducts
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
        ProductResponse productResponse = ProductResponse.newBuilder().setProduct(productRequest).setMessage("Created product success").setSuccess(true).build();

        // Send the response and mark the process as completed
        productResponseObserver.onNext(productResponse);
        productResponseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(ProductId productId, StreamObserver<ProductDeleteResponse> productDeleteResponseObserver) {
        ProductDeleteResponse productDeleteResponse = ProductDeleteResponse.newBuilder().setMessage("Delete Success").setSuccess(true).build();

        // Send the response and mark the process as completed
        productDeleteResponseObserver.onNext(productDeleteResponse);
        productDeleteResponseObserver.onCompleted();
    }
}
