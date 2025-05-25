package com.example.sodamsodam.apps.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ErrorResponse(e.getMessage()));
    }

    // 닉네임 중복 예외 처리 추가
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNicknameException(DuplicateNicknameException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ErrorResponse(e.getMessage()));
    }

    // 전화번호 중복 예외 처리 추가
    @ExceptionHandler(DuplicatePhoneNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ErrorResponse(e.getMessage()));
    }

    // 잘못된 요청 데이터 예외 처리 추가
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 오류가 발생했습니다."));
    }
}

// 에러 응답 DTO
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}