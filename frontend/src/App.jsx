import React from "react";
import Signup from "./pages/Signup";

import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./auth/AuthProvider";

// Pages
import Home from "./pages/Home";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import AdminDashboard from "./pages/AdminDashboard";
import AdminLogs from "./pages/AdminLogs";
import UsersAdmin from "./pages/UsersAdmin";
import AdminFeedbacks from "./pages/AdminFeedbacks"; // ✅ new
import UserProfile from "./pages/UserProfile";
import UserLogs from "./pages/UserLogs";

// Components
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* 🌍 Public */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />


          {/* 👤 User Routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Layout>
                  <Dashboard />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/user/profile"
            element={
              <ProtectedRoute>
                <Layout>
                  <UserProfile />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/user/logs"
            element={
              <ProtectedRoute>
                <Layout>
                  <UserLogs />
                </Layout>
              </ProtectedRoute>
            }
          />

          {/* 🛡️ Admin Routes */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <Layout>
                  <AdminDashboard />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <Layout>
                  <UsersAdmin />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/logs"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <Layout>
                  <AdminLogs />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/feedbacks"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <Layout>
                  <AdminFeedbacks />
                </Layout>
              </ProtectedRoute>
            }
          />

          {/* ❌ 404 */}
          <Route
            path="*"
            element={
              <Layout>
                <div className="text-center text-gray-500 mt-10">
                  <h2 className="text-2xl font-semibold mb-2">
                    404 - Page Not Found
                  </h2>
                  <p>Looks like this route doesn’t exist.</p>
                </div>
              </Layout>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
