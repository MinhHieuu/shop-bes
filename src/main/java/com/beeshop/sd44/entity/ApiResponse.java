package com.beeshop.sd44.entity;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private String message;
    private T data;

    private LocalDateTime timestamp;

    public ApiResponse(String message, T data) {

        this.message = message;
        this.data = data;

        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
