import { Navigate } from "react-router-dom";
import { getSession } from "../session.js";

/** Sends people to the login page unless they're signed in with the right role. */
export default function RequireRole({ role, children }) {
  const session = getSession();
  if (!session || session.role !== role) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
