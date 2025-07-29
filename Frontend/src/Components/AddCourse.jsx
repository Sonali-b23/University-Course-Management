import axios from "axios";
import React, { Fragment, useEffect, useState } from "react";
import { Button, Container, Form, FormGroup } from "react-bootstrap";
import base_url from "./../api/bootapi";
import toast, { Toaster } from "react-hot-toast";

export default function AddCourse() {
  useEffect(() => {
    document.title = "Add Courses | Project by Tanmay";
  }, []);

  // Initialize state for the course
  const [course, setCourse] = useState({
    id: "",
    title: "",
    description: "",
  });

  // Handle form submission
  const handleForm = (e) => {
    e.preventDefault(); // Prevent form default submission
    console.log(course);
    postDatatoServer(course);
  };

  // Function to post data to the server
  const postDatatoServer = (data) => {
    toast.promise(
      axios.post(`${base_url}/courses`, data),
      {
        loading: "Adding course...",
        success: "Course added successfully!",
        error: "Something went wrong!",
      }
    );
  };

  // Clear form fields
  const clearForm = () => {
    setCourse({ id: "", title: "", description: "" });
  };

  return (
    <div>
      {/* Add the Toaster component */}
      <Toaster position="top-center" reverseOrder={false} />

      <Fragment>
        <h1 className="text-center my-3">Fill Course Details</h1>
        <Form onSubmit={handleForm}>
          <FormGroup>
            <label htmlFor="userid">Course Id: </label>
            <br />
            <input
              type="text"
              placeholder="Enter Here"
              name="userid"
              id="userid"
              className="form-control"
              value={course.id}
              onChange={(e) => {
                setCourse({ ...course, id: e.target.value });
              }}
            />
          </FormGroup>
          <br />
          <FormGroup>
            <label htmlFor="title">Course Title: </label>
            <br />
            <input
              type="text"
              placeholder="Enter title here"
              id="title"
              className="form-control"
              value={course.title}
              onChange={(e) => {
                setCourse({ ...course, title: e.target.value });
              }}
            />
          </FormGroup>
          <br />
          <FormGroup>
            <label htmlFor="description">Course Description:</label>
            <br />
            <textarea
              placeholder="Enter description here"
              id="description"
              className="form-control"
              style={{ height: 200 }}
              value={course.description}
              onChange={(e) => {
                setCourse({ ...course, description: e.target.value });
              }}
            />
          </FormGroup>
          <br />
          <Container className="text-center">
            <Button type="submit" variant="success" className="me-2">
              Add Course
            </Button>
            <Button variant="warning" onClick={clearForm}>
              Clear
            </Button>
          </Container>
        </Form>
      </Fragment>
    </div>
  );
}
