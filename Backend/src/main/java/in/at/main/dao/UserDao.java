package in.at.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.at.main.entity.User;

public interface UserDao extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
}
