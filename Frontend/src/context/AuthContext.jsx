import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import httpClient, { TOKEN_STORAGE_KEY } from "../api/httpClient";
import { AuthContext } from "./authContextInstance";

const USER_STORAGE_KEY = "ucm_auth_user";

// The JWT and the logged-in user's { username, role } are kept in
// localStorage (not just React state) so a page refresh doesn't silently
// log you out -- this is a portfolio/demo app, so a plain JWT-in-
// localStorage setup is used for simplicity; a production app would
// typically prefer an httpOnly cookie to reduce XSS exposure.
export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_STORAGE_KEY));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem(USER_STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (token) {
      localStorage.setItem(TOKEN_STORAGE_KEY, token);
    } else {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(USER_STORAGE_KEY);
    }
  }, [user]);

  const applyAuthResponse = (data) => {
    setToken(data.token);
    setUser({ username: data.username, role: data.role });
  };

  const login = async (username, password) => {
    const response = await httpClient.post("/auth/login", { username, password });
    applyAuthResponse(response.data);
  };

  const register = async (username, password) => {
    const response = await httpClient.post("/auth/register", { username, password });
    applyAuthResponse(response.data);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
  };

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    isAdmin: user?.role === "ADMIN",
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
