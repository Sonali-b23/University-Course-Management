// src/components/Course.js

import { Button, Card, CardBody, CardSubtitle, CardText, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import PropTypes from "prop-types";
import httpClient from "../api/httpClient";
import { useAuth } from "../context/useAuth";

export default function Course({ course, update }) {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();

  const deleteCourse = (id) => {
    if (!window.confirm(`Delete "${course.title}"? This can't be undone.`)) {
      return;
    }

    toast
      .promise(httpClient.delete(`/courses/${id}`), {
        loading: "Deleting course...",
        success: "Course successfully deleted!",
        error: (err) =>
          err?.response?.data?.message || "Course not deleted! Server problem.",
      })
      .then(() => {
        update(id);
      });
  };

  const handleUpdate = (id) => {
    navigate(`/update-course/${id}`);
  };

  return (
    <Card className="text-center">
      <CardBody>
        <CardSubtitle className="font-weight-bold">{course.title}</CardSubtitle>
        <CardText>{course.description}</CardText>
        {isAdmin && (
          <Container className="text-center">
            <Button
              variant="danger"
              onClick={() => deleteCourse(course.id)}
            >
              Delete
            </Button>
            <Button
              variant="primary"
              style={{ marginLeft: 5 }}
              onClick={() => handleUpdate(course.id)}
            >
              Update
            </Button>
          </Container>
        )}
      </CardBody>
    </Card>
  );
}

Course.propTypes = {
  course: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
  }).isRequired,
  update: PropTypes.func.isRequired,
};
