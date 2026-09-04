package in.at.main.entity;

/**
 * ADMIN can create/update/delete courses; USER can only browse them. New
 * self-registrations always come in as USER -- see AuthController and
 * config.AdminSeeder for how the first ADMIN account gets created.
 */
public enum Role {
	ADMIN,
	USER
}
