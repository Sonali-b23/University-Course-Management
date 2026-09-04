package in.at.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import in.at.main.dao.UserDao;
import in.at.main.entity.Role;
import in.at.main.entity.User;

/**
 * There's no other way to get an ADMIN account (self-registration always
 * creates a USER, by design -- see AuthController), so this creates exactly
 * one default admin on startup if none exists yet.
 *
 * ADMIN_USERNAME / ADMIN_PASSWORD default to "admin" / "admin123" for local
 * development -- change both via environment variables before running this
 * anywhere real, and change the password immediately after first login in
 * any case (there's no "change password" endpoint yet -- see README).
 */
@Component
public class AdminSeeder implements CommandLineRunner {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.admin.username:admin}")
	private String adminUsername;

	@Value("${app.admin.password:admin123}")
	private String adminPassword;

	@Override
	public void run(String... args) {
		if (userDao.existsByUsername(adminUsername)) {
			return;
		}
		User admin = new User(adminUsername, passwordEncoder.encode(adminPassword), Role.ADMIN);
		userDao.save(admin);
		System.out.println("Seeded default admin account '" + adminUsername
				+ "' -- change ADMIN_USERNAME/ADMIN_PASSWORD (and log in and change it) before using this anywhere but your own machine.");
	}
}
