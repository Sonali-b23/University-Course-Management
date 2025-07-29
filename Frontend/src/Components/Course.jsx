// src/components/Course.js
import React from "react";
import { Button, Card, CardBody, CardSubtitle, CardText, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";  // Import useNavigate for routing
import toast from "react-hot-toast";
import axios from "axios";
import base_url from "../api/bootapi";

export default function Course({ course, update }) {
  const navigate = useNavigate();  // Initialize useNavigate hook

  const deleteCourse = (id) => {
    toast
      .promise(axios.delete(`${base_url}/courses/${id}`), {
        loading: "Deleting course...",
        success: "Course successfully deleted!",
        error: "Course not deleted! Server problem.",
      })
      .then(() => {
        update(id); // Update the course list after successful deletion
      });
  };

  // Navigate to update course page
  const handleUpdate = (id) => {
    navigate(`/update-course/${id}`); // Navigate to update page with courseId
  };

  return (
    <Card className="text-center">
      <CardBody>
        <CardSubtitle className="font-weight-bold">{course.title}</CardSubtitle>
        <CardText>{course.description}</CardText>
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
            onClick={() => handleUpdate(course.id)} // Trigger update
          >
            Update
          </Button>
        </Container>
      </CardBody>
    </Card>
  );
}
