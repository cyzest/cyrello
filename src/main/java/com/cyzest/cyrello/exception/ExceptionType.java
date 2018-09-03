package com.cyzest.cyrello.exception;

import org.springframework.http.HttpStatus;

/**
 * 익셉션 타입 인터페이스
 */
public interface ExceptionType {

    int getResultCode();
    String getResultMessage();
    HttpStatus getStatusCode();

}
