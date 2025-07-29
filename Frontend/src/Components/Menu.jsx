import React from "react";
import { ListGroup } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function Menu() {
  return (
    <div>
      <ListGroup>
        <Link className="list-group-item list-group-item-action" to="/">
          Home
        </Link>
        <Link
          className="list-group-item list-group-item-action"
          to="/add-course"
        >
          Add Course
        </Link>
        <Link
          className="list-group-item list-group-item-action"
          to="/view-courses"
        >
          View Courses
        </Link>
        <Link className="list-group-item list-group-item-action" to="/about">
          About Us
        </Link>
      </ListGroup>
    </div>
  );
}
