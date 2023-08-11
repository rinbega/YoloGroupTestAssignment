package ee.desu.yologrouptestassignment.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiErrorResponse {

    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    private final HttpStatus status;
    private final String message;
    private final List<String> errors;

    public ApiErrorResponse(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorResponse() {
        this.status = HttpStatus.BAD_REQUEST;
        this.message = "";
        this.errors = List.of();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }
}
