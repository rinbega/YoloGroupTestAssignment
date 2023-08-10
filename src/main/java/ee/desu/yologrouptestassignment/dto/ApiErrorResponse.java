package ee.desu.yologrouptestassignment.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiErrorResponse(HttpStatus status, String message, List<String> errors) {

    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";
}
