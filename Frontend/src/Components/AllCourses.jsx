import React, { useEffect, useState } from "react";
import Course from "./Course";
import base_url from "../api/bootapi";
import axios from "axios";
import toast, { Toaster } from "react-hot-toast";

export default function AllCourses() {
  useEffect(() => {
    document.title = "All Courses | Project by Tanmay";
  }, []); // [] only for one-time execution

  // Function to call server
  const getAllCoursesFromServer = () => {
    toast.promise(
      axios.get(`${base_url}/courses`),
      {
        loading: "Loading courses...",
        success: (response) => {
          console.log(response.data);
          setCourse(response.data);
          return "Courses have been loaded!";
        },
        error: "Something went wrong!",
      },
      {
        position: "bottom-center", // Toast position
      }
    );
  };

  // Calling loading course function
  useEffect(() => {
    getAllCoursesFromServer();
  }, []);

  const [courses, setCourse] = useState([]);

  const updateCourses = (id) => {
    setCourse(courses.filter((c) => c.id !== id)); // Update course list after deletion
  };

  return (
    <div>
      {/* Add the Toaster component for hot-toast */}
      <Toaster position="bottom-center" reverseOrder={false} />

      <h1 style={{ textAlign: "center" }}>All Courses</h1>
      <p style={{ textAlign: "center" }}>List of Courses are as follows:</p>
      {courses.length > 0
        ? courses.map((item, index) => (
            <Course key={item.id} course={item} update={updateCourses} />
          ))
        : "No courses"}
    </div>
  );
}
