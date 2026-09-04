import { useState, useEffect } from "react";
import { Button, Form, Container } from "react-bootstrap";
import httpClient from "../api/httpClient";
import { useParams, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

export default function UpdateCourse() {
  const { courseId } = useParams();
  const navigate = useNavigate();

  const [course, setCourse] = useState({
    id: "",
    title: "",
    description: "",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    document.title = "Update Course | Project by Tanmay";
  }, []);

  useEffect(() => {
    const fetchCourse = async () => {
      try {
        const response = await httpClient.get(`/course/${courseId}`);
        setCourse(response.data);
      } catch (error) {
        console.error("Error fetching course data", error);
        toast.error(
          error?.response?.data?.message || "Failed to fetch course data."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchCourse();
  }, [courseId]);

  const handleChange = (e) => {
    setCourse({ ...course, [e.target.name]: e.target.value });
  };

  const validate = (data) => {
    const nextErrors = {};
    if (!data.title.trim()) {
      nextErrors.title = "Course title is required.";
    }
    if (!data.description.trim()) {
      nextErrors.description = "Course description is required.";
    }
    return nextErrors;
  };

  const handleUpdate = async (e) => {
    e.preventDefault();

    const validationErrors = validate(course);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    setSubmitting(true);
    try {
      // The backend now targets the resource by URL (PUT /courses/{id}),
      // and its request body has no "id" field to send in the first place
      // (see AddCourse.jsx's comment on why).
      await httpClient.put(`/courses/${courseId}`, {
        title: course.title.trim(),
        description: course.description.trim(),
      });
      toast.success("Course updated successfully!");
      navigate("/view-courses");
    } catch (error) {
      console.error("Error updating course", error);
      toast.error(error?.response?.data?.message || "Failed to update course.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Container>
        <p className="text-center my-4">Loading course...</p>
      </Container>
    );
  }

  return (
    <Container>
      <h1 className="text-center my-4">Update Course</h1>
      <Form onSubmit={handleUpdate} noValidate>
        <Form.Group controlId="formCourseId">
          <Form.Label>Course ID</Form.Label>
          <Form.Control type="text" name="id" value={course.id} disabled />
        </Form.Group>

        <Form.Group controlId="formCourseTitle" className="mt-3">
          <Form.Label>Course Title</Form.Label>
          <Form.Control
            type="text"
            name="title"
            value={course.title}
            onChange={handleChange}
            isInvalid={!!errors.title}
          />
          <Form.Control.Feedback type="invalid">
            {errors.title}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group controlId="formCourseDescription" className="mt-3">
          <Form.Label>Course Description</Form.Label>
          <Form.Control
            as="textarea"
            rows={4}
            name="description"
            value={course.description}
            onChange={handleChange}
            isInvalid={!!errors.description}
          />
          <Form.Control.Feedback type="invalid">
            {errors.description}
          </Form.Control.Feedback>
        </Form.Group>

        <Button variant="primary" type="submit" className="mt-3" disabled={submitting}>
          {submitting ? "Updating..." : "Update Course"}
        </Button>
      </Form>
    </Container>
  );
}
