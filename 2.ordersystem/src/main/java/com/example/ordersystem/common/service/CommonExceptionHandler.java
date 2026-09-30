package com.example.ordersystem.common.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegal(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> method(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값을 확인하세요.");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> maxUpload(MaxUploadSizeExceededException e) {
        return new ResponseEntity<>("업로드 가능한 파일 크기를 초과했습니다.", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
