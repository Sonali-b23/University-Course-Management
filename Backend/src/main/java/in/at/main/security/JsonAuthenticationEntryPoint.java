package in.at.main.security;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles requests to a protected endpoint with no (or an invalid) token.
 * This runs inside the security filter chain, before the request ever
 * reaches a controller -- so it can't go through GlobalExceptionHandler,
 * but produces the same JSON error shape for consistency.
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
		body.put("message", "Authentication is required to access this resource");

		objectMapper.writeValue(response.getOutputStream(), body);
	}
}
