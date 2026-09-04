package in.at.main.exception;

/**
 * Thrown when a user tries to enroll in a course they're already registered
 * for. Handled by GlobalExceptionHandler as a 409, mirroring how
 * DuplicateUsernameException is handled for registration.
 */
public class AlreadyEnrolledException extends RuntimeException {
	public AlreadyEnrolledException(String message) {
		super(message);
	}
}
