import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../auth/AuthProvider";

export default function ProtectedRoute({ children, roles }) {
  const { user, loading } = useContext(AuthContext);

  if (loading) return <p>Loading...</p>;

  if (!user) return <Navigate to="/login" />;

  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/dashboard" />;
  }

  return children;
}
