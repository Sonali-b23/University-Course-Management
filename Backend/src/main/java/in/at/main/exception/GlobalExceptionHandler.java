package in.at.main.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralizes error handling for every controller so callers get
 * consistent, structured JSON error responses instead of either a raw
 * Spring stack-trace page (unhandled RuntimeException) or an
 * over-broad 500 for what's actually a 404 or a validation problem.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

		Map<String, Object> body = errorBody(HttpStatus.BAD_REQUEST, "Validation failed");
		body.put("fieldErrors", fieldErrors);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateUsername(DuplicateUsernameException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(AlreadyEnrolledException.class)
	public ResponseEntity<Map<String, Object>> handleAlreadyEnrolled(AlreadyEnrolledException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(HttpStatus.CONFLICT, ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(errorBody(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
		// Deliberately does not include ex.getMessage() / stack trace in the
		// response -- an internal error's details shouldn't leak to the client.
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later."));
	}

	private Map<String, Object> errorBody(HttpStatus status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return body;
	}
}
