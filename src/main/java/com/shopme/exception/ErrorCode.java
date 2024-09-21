package com.shopme.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED("User not existed", HttpStatus.NOT_FOUND),
    USERID_NOT_FOUND("Could not found user id", HttpStatus.NOT_FOUND),
    USERNAME_NOT_FOUND("Could not found username", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Password must be at least 8 character", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED("Exist email", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED("Exist username", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL("Password is incorrect", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Token invalid", HttpStatus.UNAUTHORIZED),
    CATEGORY_ID_NOT_FOUND("Could not found category", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTED("Exist Category Name", HttpStatus.BAD_REQUEST),
    CATEGORY_ALIAS_EXISTED("Exist Category Alias", HttpStatus.BAD_REQUEST),
;

    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
