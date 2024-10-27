package com.example.demo.controller;

import com.example.demo.UserRequest;
import com.example.demo.UserResponse;
import com.example.demo.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserController extends UserServiceGrpc.UserServiceImplBase{
    @Override
    public void createUser(UserRequest userReq, StreamObserver<UserResponse> userResponseObserver) {
        System.out.println("--- created user executed ---");

        UserResponse userResponse = UserResponse.newBuilder().setUser(userReq).setSuccess(true).build();

        // Send the response and mark the process as completed
        userResponseObserver.onNext(userResponse);
        userResponseObserver.onCompleted();
    }
}
