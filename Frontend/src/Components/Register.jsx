import { useEffect, useState } from "react";
import { Button, Container, Form } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom";
import toast from "react-hot-toast";
import { useAuth } from "../context/useAuth";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    document.title = "Register | Project by Tanmay";
  }, []);

  const [form, setForm] = useState({ username: "", password: "" });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validate = (data) => {
    const nextErrors = {};
    if (data.username.trim().length < 3) {
      nextErrors.username = "Username must be at least 3 characters.";
    }
    if (data.password.length < 8) {
      nextErrors.password = "Password must be at least 8 characters.";
    }
    return nextErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = validate(form);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    setSubmitting(true);
    try {
      await register(form.username.trim(), form.password);
      toast.success("Account created!");
      navigate("/", { replace: true });
    } catch (error) {
      const serverFieldErrors = error?.response?.data?.fieldErrors;
      if (serverFieldErrors) {
        setErrors(serverFieldErrors);
      } else {
        toast.error(error?.response?.data?.message || "Registration failed.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Container>
      <h1 className="text-center my-4">Create an Account</h1>
      <p className="text-center text-muted">
        New accounts can browse courses. Only an admin account can add, update, or
        delete them.
      </p>
      <Form onSubmit={handleSubmit} noValidate>
        <Form.Group className="mb-3" controlId="registerUsername">
          <Form.Label>Username</Form.Label>
          <Form.Control
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            isInvalid={!!errors.username}
          />
          <Form.Control.Feedback type="invalid">{errors.username}</Form.Control.Feedback>
        </Form.Group>
        <Form.Group className="mb-3" controlId="registerPassword">
          <Form.Label>Password</Form.Label>
          <Form.Control
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            isInvalid={!!errors.password}
          />
          <Form.Control.Feedback type="invalid">{errors.password}</Form.Control.Feedback>
        </Form.Group>
        <Button type="submit" variant="success" disabled={submitting}>
          {submitting ? "Creating account..." : "Register"}
        </Button>
      </Form>
      <p className="mt-3 text-center">
        Already have an account? <Link to="/login">Log In</Link>
      </p>
    </Container>
  );
}
