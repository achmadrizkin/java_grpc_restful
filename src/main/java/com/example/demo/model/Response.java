package com.example.demo.model;

public class Response<T> {
    private int statusCode;
    private T payload;
    private String message;

    public Response() {

    }

    public Response(int statusCode, T payload, String message) {
        this.statusCode = statusCode;
        this.payload = payload;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
