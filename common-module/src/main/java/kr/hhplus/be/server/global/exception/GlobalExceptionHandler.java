package kr.hhplus.be.server.global.exception;

import kr.hhplus.be.server.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException e) {
        ApiResponse<Void> response = ApiResponse.error(404, e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException e) {
        ApiResponse<Void> response = ApiResponse.error(409, e.getMessage());
        return ResponseEntity.status(409).body(response);
    }
}
