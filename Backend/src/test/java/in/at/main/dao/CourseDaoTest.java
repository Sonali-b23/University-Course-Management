package in.at.main.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import in.at.main.entity.Course;

@DataJpaTest
class CourseDaoTest {

	@Autowired
	private CourseDao courseDao;

	/**
	 * Regression test for the original bug: Course.id had no
	 * @GeneratedValue, so a client-supplied id that matched an existing
	 * course silently overwrote it. Now that ids are always
	 * database-generated (courses are only ever constructed without an id
	 * in application code), two courses saved back-to-back must get two
	 * distinct ids -- never the same one.
	 */
	@Test
	void savingTwoCoursesWithoutAnIdAssignsDistinctGeneratedIds() {
		Course first = courseDao.save(new Course("Intro to Java", "Basics of Java"));
		Course second = courseDao.save(new Course("History 101", "A totally different course"));

		assertNotEquals(0, first.getId(), "Expected the database to assign a generated id");
		assertNotEquals(0, second.getId(), "Expected the database to assign a generated id");
		assertNotEquals(first.getId(), second.getId(),
				"Two different courses must never end up with the same id -- this was the original overwrite bug");
		assertEquals(2, courseDao.findAll().size());
	}
}
