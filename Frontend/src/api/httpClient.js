import axios from "axios";
import base_url from "./bootapi";

// A shared axios instance so every authenticated request automatically
// carries the JWT, instead of every component having to remember to add
// it. The token is stored in localStorage under this key by AuthContext.
export const TOKEN_STORAGE_KEY = "ucm_auth_token";

const httpClient = axios.create({
  baseURL: base_url,
});

httpClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default httpClient;
