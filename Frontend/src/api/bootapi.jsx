// Reads VITE_API_BASE_URL from the environment (see .env.example) so the
// deployed frontend can point at a real backend URL without a code change.
// Falls back to the local dev backend if it isn't set.
const base_url = import.meta.env.VITE_API_BASE_URL || "http://localhost:8082";
export default base_url;
