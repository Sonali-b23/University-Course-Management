import React, { useState, useEffect } from "react";
import axios from "axios";
import { Button, Form, Container } from "react-bootstrap";
import base_url from "../api/bootapi";
import { useParams } from "react-router-dom";  
import toast, { Toaster } from "react-hot-toast"; 

export default function UpdateCourse() {
  const { courseId } = useParams();  

  const [course, setCourse] = useState({
    id: "",
    title: "",
    description: "",
  });

  useEffect(() => {
    const fetchCourse = async () => {
      try {
        const response = await axios.get(`${base_url}/course/${courseId}`);
        setCourse(response.data);
      } catch (error) {
        console.error("Error fetching course data", error);
        toast.error("Failed to fetch course data."); 
      }
    };

    fetchCourse();
  }, [courseId]);

  const handleChange = (e) => {
    setCourse({ ...course, [e.target.name]: e.target.value });
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.put(`${base_url}/courses`, course);
      toast.success("Course updated successfully!"); 
    } catch (error) {
      console.error("Error updating course", error);
      toast.error("Failed to update course.");
    }
  };

  return (
    <Container>
      <Toaster position="top-center" reverseOrder={false} />
      <h1 className="text-center my-4">Update Course</h1>
      <Form onSubmit={handleUpdate}>
        <Form.Group controlId="formCourseId">
          <Form.Label>Course ID</Form.Label>
          <Form.Control
            type="text"
            name="id"
            value={course.id}
            onChange={handleChange}
            disabled
          />
        </Form.Group>

        <Form.Group controlId="formCourseTitle">
          <Form.Label>Course Title</Form.Label>
          <Form.Control
            type="text"
            name="title"
            value={course.title}
            onChange={handleChange}
          />
        </Form.Group>

        <Form.Group controlId="formCourseDescription">
          <Form.Label>Course Description</Form.Label>
          <Form.Control
            as="textarea"
            rows={4}
            name="description"
            value={course.description}
            onChange={handleChange}
          />
        </Form.Group>

        <Button variant="primary" type="submit" className="mt-3">
          Update Course
        </Button>
      </Form>
    </Container>
  );
}
