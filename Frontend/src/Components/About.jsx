import React from "react";

export default function About() {
  return (
    <div className="container">
      <h1 className="text-center my-4">About the Course Management System</h1>
      <p>
        Welcome to the <strong>Course Management System</strong>. This web
        application allows users to manage courses through various operations
        such as adding, updating, deleting, and viewing courses.
      </p>
      <p>
        The frontend of the application is built with <strong>React</strong>{" "}
        (version 18.3.1) and styled using <strong>Bootstrap</strong>. The
        backend is powered by <strong>Spring Boot</strong>
        (version 3.4.2), which serves as the REST API for handling CRUD
        operations.
      </p>
      <p>
        The application communicates between the React frontend and the Spring
        Boot backend using
        <strong>Axios</strong> for API requests. The backend runs on{" "}
        <strong>localhost:8082</strong>, while the React frontend runs on{" "}
        <strong>localhost:5173</strong>.
      </p>
      <p>With this system, you can perform the following CRUD operations:</p>
      <ul>
        <li>
          <strong>Create</strong> - Add new courses to the system.
        </li>
        <li>
          <strong>Read</strong> - View the list of all available courses.
        </li>
        <li>
          <strong>Update</strong> - Modify course details.
        </li>
        <li>
          <strong>Delete</strong> - Remove courses from the system.
        </li>
      </ul>
      <p>
        The <strong>Course Management System</strong> is designed to be simple
        yet powerful, allowing efficient management of course-related data.
      </p>
    </div>
  );
}
