import PropTypes from "prop-types";
import { Navigate, useLocation } from "react-router-dom";
import { Container } from "react-bootstrap";
import { useAuth } from "../context/useAuth";

// Wraps a route that requires an ADMIN account (adding/updating/deleting
// courses). An unauthenticated visitor is sent to /login; a logged-in
// non-admin sees a plain "not allowed" message rather than being silently
// redirected, so it's clear *why* they can't get in.
export default function ProtectedRoute({ children }) {
  const { isAuthenticated, isAdmin } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  if (!isAdmin) {
    return (
      <Container className="text-center my-4">
        <h2>Admins only</h2>
        <p>Your account doesn&apos;t have permission to manage courses.</p>
      </Container>
    );
  }

  return children;
}

ProtectedRoute.propTypes = {
  children: PropTypes.node.isRequired,
};
