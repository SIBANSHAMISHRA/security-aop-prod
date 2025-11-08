// src/components/Nav.jsx (snippet)
import React from "react";
import { useNavigate } from "react-router-dom";

export default function Nav() {
  const nav = useNavigate();
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    nav("/login");
  };

  return (
    // ... your nav UI
    <button onClick={logout} className="btn-logout">Logout</button>
  );
}
