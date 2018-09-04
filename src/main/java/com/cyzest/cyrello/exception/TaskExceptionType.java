package com.cyzest.cyrello.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskExceptionType implements ExceptionType {

    // 존재하지 않는 태스크
    NOT_EXIST_TASK(4101, "Not Exist Task", HttpStatus.BAD_REQUEST),

    // 유효하지 않은 참조 태스크 포함
    CONTAINS_INVALID_REL_TASK(4102, "Contains Invalid Relation Task", HttpStatus.BAD_REQUEST),

    // 완료처리 된 참조 태스크 포함
    CONTAINS_COMPLETED_REL_TASK(4103, "Contains Completed Relation Task", HttpStatus.BAD_REQUEST),

    // 역 참조 태스크 존재
    EXIST_INVERSE_REL_TASK(4104, "Exist Inverse Relation Task", HttpStatus.BAD_REQUEST),

    // 완료처리 되지 않은 역 참조 태스크 존재
    EXIST_NON_COMPLETE_INVERSE_REL_TASK(4105, "Exist Non Complete Inverse Relation Task", HttpStatus.BAD_REQUEST),

    // 완료처리 된 태스크
    COMPLETED_TASK(4106, "Completed Task", HttpStatus.BAD_REQUEST);

    private final int resultCode;
    private final String resultMessage;
    private final HttpStatus statusCode;

    TaskExceptionType(int resultCode, String resultMessage, HttpStatus statusCode) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.statusCode = statusCode;
    }

}
