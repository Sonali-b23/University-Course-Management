import { Container, Row, Col, Button } from "react-bootstrap";
import { useEffect } from "react";

export default function Home() {
  const handleStartUsing = () => {
    alert("Let's start learning with Tanmay!");
  };

  useEffect(() => {
    document.title = "Home | Project By Tanmay";
  }, []);

  return (
    <div style={{ backgroundColor: "lightgray" }}>
      <Container className="text-center">
        <Row>
          <Col>
            <h1>Learn with Tanmay</h1>
            <p>
              This platform is developed by TanCode for learning purposes. The
              backend is powered by Spring Boot, and the frontend is built with
              React JS.
            </p>
          </Col>
        </Row>
      </Container>
      <Container className="d-flex justify-content-center mt-4">
        <Button variant="primary" onClick={handleStartUsing}>
          Start Using
        </Button>
      </Container>
      <hr></hr>
    </div>
  );
}
