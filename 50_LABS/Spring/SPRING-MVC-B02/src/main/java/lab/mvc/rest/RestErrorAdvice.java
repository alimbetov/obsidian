package lab.mvc.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorAdvice {

    @ExceptionHandler(CatalogNotFoundException.class)
    public ResponseEntity<ApiError> notFound(CatalogNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(new ApiError("CATALOG_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError("VALIDATION_FAILED", "Request body failed validation"));
    }
}
