package com.beeshop.sd44.service.error;

import com.beeshop.sd44.entity.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi @Valid trên @RequestBody (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = new java.util.ArrayList<>(ex.getBindingResult().getAllErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .toList());
        List<String> customErrors = ex.getBindingResult().getAllErrors()
                .stream()
                .filter(err -> err.getDefaultMessage() != null && err.getDefaultMessage().startsWith("CustomError:"))
                .map(err -> err.getDefaultMessage().substring("CustomError:".length()))
                .toList();
        errors.addAll(customErrors);
        StringBuilder errorMessage = new StringBuilder();

        for(String error : errors) {
            errorMessage.append(error).append("\n");
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Du lieu khong hop le", errorMessage));
    }

    // Bắt IllegalArgumentException từ service
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    // Bắt tất cả exception còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("He thong dang co loi, vui long thu lai sau", null));
    }
}

