package kr.hhplus.be.server.global.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<T>(status, message, null);
    }
}
