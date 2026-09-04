import { Col, Row, Container } from "react-bootstrap";
import { Toaster } from "react-hot-toast";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import Home from "./Components/Home";
import AllCourses from "./Components/AllCourses";
import AddCourse from "./Components/AddCourse";
import Header from "./Components/Header";
import Menu from "./Components/Menu";
import About from "./Components/About";
import UpdateCourse from "./Components/UpdateCourse";
import Login from "./Components/Login";
import Register from "./Components/Register";
import ProtectedRoute from "./Components/ProtectedRoute";
import { AuthProvider } from "./context/AuthContext";

function App() {

  return (
    <div>
      <AuthProvider>
        <Router>
          {/* A single, app-wide toast host -- previously every page mounted
              its own <Toaster/>, and react-toastify's <ToastContainer/> was
              also mounted here but never actually used anywhere (every real
              toast call used react-hot-toast). */}
          <Toaster position="top-center" reverseOrder={false} />

          <Container>
            <Header />

            <Row>
              <Col md={4}>
                <Menu />
              </Col>
              <Col md={8}>
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route
                    path="/add-course"
                    element={
                      <ProtectedRoute>
                        <AddCourse />
                      </ProtectedRoute>
                    }
                  />
                  <Route path="/view-courses" element={<AllCourses />} />
                  <Route path="/about" element={<About/>} />
                  <Route
                    path="/update-course/:courseId"
                    element={
                      <ProtectedRoute>
                        <UpdateCourse />
                      </ProtectedRoute>
                    }
                  />
                  <Route path="/login" element={<Login />} />
                  <Route path="/register" element={<Register />} />
                </Routes>
              </Col>
            </Row>
          </Container>
        </Router>
      </AuthProvider>
    </div>
  );
}

export default App;
