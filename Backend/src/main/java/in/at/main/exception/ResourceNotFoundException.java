package in.at.main.exception;

/**
 * Thrown when a course id is looked up (get/update/delete) and no such
 * course exists. Handled by GlobalExceptionHandler and turned into a clean
 * 404 -- previously this was a generic RuntimeException that either leaked
 * as a raw 500 with a stack trace, or (for delete only) was caught and
 * flattened into a misleading 500 "server error".
 */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
