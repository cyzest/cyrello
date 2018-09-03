package com.cyzest.cyrello.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResponse {

    private int code;

    private String message;

    private Object extra;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", extra=" + extra +
                '}';
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
