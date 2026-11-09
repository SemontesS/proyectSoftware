package application.adapters.rest.exception;

import application.adapters.rest.dtos.responses.ErrorResponseDTO;
import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Translates domain and validation errors into HTTP responses with a uniform body. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                     HttpServletRequest request) {
        return respond(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedOperationException ex,
                                                               HttpServletRequest request) {
        return respond(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler({DuplicateEntityException.class, BusinessRuleViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return respond(HttpStatus.BAD_REQUEST, "Validation failed.", request, fieldErrors);
    }

    /** Invalid values that the domain rejects while building value objects (email, password, enums...). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex,
                                                                  HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDTO> handleMalformedRequest(Exception ex, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "Malformed request.", request, null);
    }

    /** Last resort: the cause is logged but never exposed to the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex, HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.error("Unexpected error [requestId={}] {} {}", requestId, request.getMethod(), request.getRequestURI(), ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error. Reference: " + requestId, request, requestId, null);
    }

    private ResponseEntity<ErrorResponseDTO> respond(HttpStatus status, String message, HttpServletRequest request,
                                                     Map<String, String> fieldErrors) {
        String requestId = UUID.randomUUID().toString();
        log.warn("{} [requestId={}] {} {} -> {}", status.value(), requestId, request.getMethod(),
                request.getRequestURI(), message);
        return body(status, message, request, requestId, fieldErrors);
    }

    private ResponseEntity<ErrorResponseDTO> body(HttpStatus status, String message, HttpServletRequest request,
                                                  String requestId, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(Instant.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI(), requestId, fieldErrors));
    }
}
