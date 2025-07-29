import React from "react";
import { Col, Row, Container } from "react-bootstrap";
import { ToastContainer } from "react-toastify";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "bootstrap/dist/css/bootstrap.min.css";
import Home from "./Components/Home";
import AllCourses from "./Components/AllCourses";
import AddCourse from "./Components/AddCourse";
import Header from "./Components/Header";
import Menu from "./Components/Menu";
import About from "./Components/About";
import UpdateCourse from "./Components/UpdateCourse";

function App() {

  return (
    <div>
      <Router>
        <ToastContainer />
        
        <Container>
          <Header />

          <Row>
            <Col md={4}>
              <Menu />
            </Col>
            <Col md={8}>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/add-course" element={<AddCourse />} />
                <Route path="/view-courses" element={<AllCourses />} />
                <Route path="/about" element={<About/>} />
                <Route path="/update-course/:courseId" element={<UpdateCourse />} />
              </Routes>
            </Col>
          </Row>
        </Container>
      </Router>
    </div>
  );
}

export default App;
