package com.cyzest.cyrello.dto;

import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

@ToString
public class ApiResponse {

    private int code;

    private String message;

    private Object extra;

    public ApiResponse() {
        this.code = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(HttpStatus httpStatus) {
        Assert.notNull(httpStatus, "httpStatus must not be null");
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    @SuppressWarnings("unchecked")
    public void putExtra(String key, Object value) {
        if (!(extra instanceof Map)) {
            extra = new LinkedHashMap<String, Object>();
        }
        Map<String, Object> map = (Map<String, Object>) extra;
        map.put(key, value);
    }

}
