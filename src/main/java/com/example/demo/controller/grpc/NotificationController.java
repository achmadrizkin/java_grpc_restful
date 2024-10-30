package com.example.demo.controller.grpc;

import com.example.demo.Notification;
import com.example.demo.NotificationResponse;
import com.example.demo.NotificationServiceGrpc;
import com.example.demo.producer.NotificationProducer;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class NotificationController extends NotificationServiceGrpc.NotificationServiceImplBase {
    @Autowired
    private NotificationProducer notificationProducer;

    @Override
    public void sendNotification(Notification notification, StreamObserver<NotificationResponse> responseObserver) {
        System.out.println("--- send notification executed ---");

        Notification notificationRpc = Notification.newBuilder()
                .setId(notification.getId())
                .setTitle(notification.getTitle())
                .setMessage(notification.getMessage())
                .setType(notification.getType())
                .build();

        NotificationResponse response = NotificationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Success sending message")
                .setNotification(notificationRpc)
                .build();

        System.out.println("--- data: " + response + "---");

        notificationProducer.sendMessage(notificationRpc);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
