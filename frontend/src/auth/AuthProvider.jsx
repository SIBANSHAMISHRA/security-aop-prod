// src/auth/AuthProvider.jsx
import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const nav = useNavigate();

  // ✅ Auto-load user info if token exists
  useEffect(() => {
    const loadUser = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setLoading(false);
        return;
      }

      try {
        const res = await api.get("/auth/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUser({
          email: res.data.email,
          role: res.data.role,
          token,
        });
      } catch (err) {
        console.error("Auth load failed:", err);
        localStorage.removeItem("token");
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, []);

  // ✅ Login function
  const login = async (email, password) => {
    try {
      console.log("🟢 Attempting login:", email);
      const res = await api.post("/auth/login", { email, password });
      console.log("✅ Response:", res.data);

      if (res.data.status === "success") {
        const { token, email, role } = res.data;

        // Save to localStorage
        localStorage.setItem("token", token);
        localStorage.setItem("email", email);
        localStorage.setItem("role", role);

        // Update context
        setUser({ email, role, token });

        // Redirect based on role
        if (role === "ADMIN") nav("/admin");
        else nav("/dashboard");

        return { ok: true };
      } else {
        return { ok: false, message: res.data.message || "Invalid credentials" };
      }
    } catch (err) {
      console.error("❌ Login failed:", err);
      return { ok: false, message: "Server error or invalid credentials" };
    }
  };

  // ✅ Logout
  const logout = () => {
    localStorage.clear();
    setUser(null);
    nav("/login");
  };

  return (
    <AuthContext.Provider value={{ user, setUser, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

// ✅ Helper hook
export function useAuth() {
  return useContext(AuthContext);
}
