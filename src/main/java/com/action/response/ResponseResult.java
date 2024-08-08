package com.action.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseResult<T> {

    /**
     * response timestamp.
     */
    private long timestamp;

    /**
     * response code, 200 -> OK.
     */
    private String status;

    /**
     * response message.
     */
    private String message;

    /**
     * response data.
     */
    private T data;


    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder().data(data)
                .message(ResponseCode.SUCCESS.getDescription())
                .status(ResponseCode.SUCCESS.getResponseCode())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T extends Serializable> ResponseResult<T> fail(String message) {
        return fail(null, message);
    }


    /**
     *
     * @param data
     * @param message
     * @return
     * @param <T>
     */
    public static <T> ResponseResult<T> fail(T data, String message) {
        return ResponseResult.<T>builder().data(data)
                .message(message)
                .status(ResponseCode.FAIL.getResponseCode())
                .timestamp(System.currentTimeMillis())
                .build();
    }


}
