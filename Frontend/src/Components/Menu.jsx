import { Button, ListGroup } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { useAuth } from "../context/useAuth";

export default function Menu() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success("Logged out.");
    navigate("/");
  };

  return (
    <div>
      <ListGroup>
        <Link className="list-group-item list-group-item-action" to="/">
          Home
        </Link>
        {isAdmin && (
          <Link
            className="list-group-item list-group-item-action"
            to="/add-course"
          >
            Add Course
          </Link>
        )}
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

      <div className="mt-3 text-center">
        {isAuthenticated ? (
          <>
            <p className="mb-2">
              Signed in as <strong>{user.username}</strong> ({user.role})
            </p>
            <Button variant="outline-secondary" size="sm" onClick={handleLogout}>
              Log Out
            </Button>
          </>
        ) : (
          <ListGroup>
            <Link className="list-group-item list-group-item-action" to="/login">
              Log In
            </Link>
            <Link className="list-group-item list-group-item-action" to="/register">
              Register
            </Link>
          </ListGroup>
        )}
      </div>
    </div>
  );
}
