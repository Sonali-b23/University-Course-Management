package in.at.main.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import in.at.main.entity.Course;

public interface CourseDao extends JpaRepository<Course, Long> {

}
