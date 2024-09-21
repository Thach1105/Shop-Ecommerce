package com.shopme.exception;

import com.shopme.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNAUTHENTICATED.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception){

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());

        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }

    @ExceptionHandler(value = AuthenticationServiceException.class)
    ResponseEntity<ApiResponse<?>> handlingAuthenticationServiceException(
            AuthenticationServiceException exception){


        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNAUTHENTICATED.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = JwtException.class)
    ResponseEntity<ApiResponse<?>> handlingJwtException(JwtException exception) {

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(ErrorCode.INVALID_TOKEN.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_TOKEN.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException(Exception exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("ERROR");
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }
}
