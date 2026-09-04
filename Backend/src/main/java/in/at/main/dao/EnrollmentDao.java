package in.at.main.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.at.main.entity.Enrollment;

public interface EnrollmentDao extends JpaRepository<Enrollment, Long> {
	boolean existsByUser_UsernameAndCourse_Id(String username, long courseId);

	Optional<Enrollment> findByUser_UsernameAndCourse_Id(String username, long courseId);

	List<Enrollment> findByUser_Username(String username);

	List<Enrollment> findByCourse_Id(long courseId);
}
