package in.at.main.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.at.main.dao.UserDao;
import in.at.main.dto.AuthResponse;
import in.at.main.dto.LoginRequest;
import in.at.main.dto.RegisterRequest;
import in.at.main.entity.Role;
import in.at.main.entity.User;
import in.at.main.exception.DuplicateUsernameException;
import in.at.main.security.CustomUserDetailsService;
import in.at.main.security.JwtService;
import jakarta.validation.Valid;

@RestController
public class AuthController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	/**
	 * Self-registration always creates a USER, never an ADMIN -- there's no
	 * way for a client to grant themselves elevated permissions this way.
	 * See config.AdminSeeder for how the one ADMIN account gets created.
	 */
	@PostMapping("/auth/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		if (userDao.existsByUsername(request.getUsername())) {
			throw new DuplicateUsernameException("Username '" + request.getUsername() + "' is already taken");
		}

		User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), Role.USER);
		userDao.save(user);

		String token = jwtService.generateToken(
				userDetailsService.loadUserByUsername(user.getUsername()),
				Map.of("role", user.getRole().name()));

		return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, user.getUsername(), user.getRole()));
	}

	@PostMapping("/auth/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		// Throws BadCredentialsException on a wrong username/password, which
		// GlobalExceptionHandler turns into a clean 401.
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		User user = userDao.findByUsername(request.getUsername())
				.orElseThrow(() -> new IllegalStateException(
						"Authenticated username has no matching user record: " + request.getUsername()));

		String token = jwtService.generateToken(
				userDetailsService.loadUserByUsername(user.getUsername()),
				Map.of("role", user.getRole().name()));

		return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole()));
	}
}
