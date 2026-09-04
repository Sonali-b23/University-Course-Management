import { useEffect, useState } from "react";
import Course from "./Course";
import httpClient from "../api/httpClient";
import toast from "react-hot-toast";

export default function AllCourses() {
  const [courses, setCourse] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    document.title = "All Courses | Project by Tanmay";
  }, []);

  useEffect(() => {
    const getAllCoursesFromServer = () => {
      toast
        .promise(httpClient.get(`/courses`), {
          loading: "Loading courses...",
          success: (response) => {
            setCourse(response.data);
            return "Courses have been loaded!";
          },
          error: "Something went wrong!",
        })
        .finally(() => setLoading(false));
    };

    getAllCoursesFromServer();
  }, []); // one-time load on mount

  const updateCourses = (id) => {
    setCourse((current) => current.filter((c) => c.id !== id));
  };

  return (
    <div>
      <h1 style={{ textAlign: "center" }}>All Courses</h1>
      <p style={{ textAlign: "center" }}>List of Courses are as follows:</p>
      {loading ? (
        <p style={{ textAlign: "center" }}>Loading...</p>
      ) : courses.length > 0 ? (
        courses.map((item) => (
          <Course key={item.id} course={item} update={updateCourses} />
        ))
      ) : (
        <p style={{ textAlign: "center" }}>No courses yet -- add one to get started.</p>
      )}
    </div>
  );
}
