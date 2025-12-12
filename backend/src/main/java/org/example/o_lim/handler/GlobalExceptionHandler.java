package org.example.o_lim.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.o_lim.common.enums.ErrorCode;
import org.example.o_lim.common.errors.ErrorResponse;
import org.example.o_lim.common.errors.FieldErrorItem;
import org.example.o_lim.dto.ResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ResponseDto<Object>> failed(
            ErrorCode code, String reason, List<FieldErrorItem> errors
            ) {
        String finalReason = (reason != null && !reason.isBlank()) ? reason : code.defaultMessage;
        ErrorResponse body = ErrorResponse.of(code.code, finalReason, errors);

        return ResponseEntity.status(code.status)
                .body(ResponseDto.setFailed(finalReason,body));
    }

    private List<FieldErrorItem> toFieldErrors(MethodArgumentNotValidException e) {
        List<FieldErrorItem> list = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            list.add(new FieldErrorItem(
                    err.getField(),
                    Objects.toString(err.getRejectedValue(), "null"),
                    err.getDefaultMessage()
            ));
        });

        e.getBindingResult().getGlobalErrors().forEach(err -> {
            list.add(new FieldErrorItem(err.getObjectName(), "", err.getDefaultMessage()));
        });

        return list;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ResponseDto<Object>> handleBadRequest(Exception e) {
        log.warn("Bad Request: {}", e.getMessage());

        return failed(ErrorCode.BAD_REQUEST, null, null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseDto<Object>> handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());

        return failed(ErrorCode.VALIDATION_ERROR, null, toFieldErrors(e));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ResponseDto<Object>> handleAuth(AuthenticationException e) {
        log.warn("Unauthorized: {}", e.getMessage());

        return failed(ErrorCode.UNAUTHORIZED, null, null);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ResponseDto<Object>> handleAccessDenied(AccessDeniedException e) {
        log.warn("AccessDenied: {}", e.getMessage());

        return failed(ErrorCode.FORBIDDEN, null, null);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ResponseDto<Object>> handleNotFound(EntityNotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());

        return failed(ErrorCode.NOT_FOUND, null, null);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ResponseDto<Object>> handleConflict(DataIntegrityViolationException e) {
        log.warn("Conflict: {}", e.getMessage());

        return failed(ErrorCode.CONFLICT, null, null);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseDto<Object>> handleException(Exception e) {
        log.error("Internal Error: {}", e.getMessage());

        return failed(ErrorCode.INTERNAL_ERROR, null, null);
    }
}

