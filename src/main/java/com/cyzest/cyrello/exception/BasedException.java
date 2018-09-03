package com.cyzest.cyrello.exception;

@SuppressWarnings("serial")
public class BasedException extends Exception {
    
    private ExceptionType exceptionType;

    private Object exceptionData = null;

    public BasedException(ExceptionType exceptionType) {
        super(exceptionType.getResultMessage());
        this.exceptionType = exceptionType;
    }

    public BasedException(ExceptionType exceptionType, Object exceptionData) {
        super(exceptionType.getResultMessage());
        this.exceptionType = exceptionType;
        this.exceptionData = exceptionData;
    }

    public BasedException(ExceptionType exceptionType, Throwable e) {
        super(exceptionType.getResultMessage(), e);
        this.exceptionType = exceptionType;
    }

    public BasedException(ExceptionType exceptionType, Object exceptionData, Throwable e) {
        super(exceptionType.getResultMessage(), e);
        this.exceptionType = exceptionType;
        this.exceptionData = exceptionData;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public Object getExceptionData() {
        return exceptionData;
    }
    
}
