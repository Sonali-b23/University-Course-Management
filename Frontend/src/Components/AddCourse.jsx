import { Fragment, useEffect, useState } from "react";
import { Button, Container, Form, FormGroup } from "react-bootstrap";
import httpClient from "./../api/httpClient";
import toast from "react-hot-toast";

export default function AddCourse() {
  useEffect(() => {
    document.title = "Add Courses | Project by Tanmay";
  }, []);

  // No "id" field: ids are always assigned by the database now (see the
  // backend's Course.id @GeneratedValue) -- the old form let a user type in
  // any id, and typing one that already existed silently overwrote that
  // course. Removing the field removes the possibility entirely.
  const [course, setCourse] = useState({
    title: "",
    description: "",
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

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

  const handleForm = (e) => {
    e.preventDefault();

    const validationErrors = validate(course);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    postDatatoServer({
      title: course.title.trim(),
      description: course.description.trim(),
    });
  };

  const postDatatoServer = (data) => {
    setSubmitting(true);
    toast
      .promise(httpClient.post(`/courses`, data), {
        loading: "Adding course...",
        success: "Course added successfully!",
        error: (err) =>
          err?.response?.data?.message || "Something went wrong!",
      })
      .then(() => clearForm())
      .finally(() => setSubmitting(false));
  };

  const clearForm = () => {
    setCourse({ title: "", description: "" });
    setErrors({});
  };

  return (
    <div>
      <Fragment>
        <h1 className="text-center my-3">Fill Course Details</h1>
        <Form onSubmit={handleForm} noValidate>
          <FormGroup>
            <label htmlFor="title">Course Title: </label>
            <br />
            <input
              type="text"
              placeholder="Enter title here"
              id="title"
              className={`form-control ${errors.title ? "is-invalid" : ""}`}
              value={course.title}
              onChange={(e) => {
                setCourse({ ...course, title: e.target.value });
              }}
            />
            {errors.title && (
              <div className="invalid-feedback d-block">{errors.title}</div>
            )}
          </FormGroup>
          <br />
          <FormGroup>
            <label htmlFor="description">Course Description:</label>
            <br />
            <textarea
              placeholder="Enter description here"
              id="description"
              className={`form-control ${errors.description ? "is-invalid" : ""}`}
              style={{ height: 200 }}
              value={course.description}
              onChange={(e) => {
                setCourse({ ...course, description: e.target.value });
              }}
            />
            {errors.description && (
              <div className="invalid-feedback d-block">
                {errors.description}
              </div>
            )}
          </FormGroup>
          <br />
          <Container className="text-center">
            <Button
              type="submit"
              variant="success"
              className="me-2"
              disabled={submitting}
            >
              {submitting ? "Adding..." : "Add Course"}
            </Button>
            <Button variant="warning" onClick={clearForm} disabled={submitting}>
              Clear
            </Button>
          </Container>
        </Form>
      </Fragment>
    </div>
  );
}
