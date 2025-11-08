import React, { useState } from "react";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const res = await login(email, password);
    if (!res.ok) setError(res.message);
    setLoading(false);
  };

  return (
    <div className="max-w-md mx-auto mt-12 p-6 bg-white rounded-xl shadow-lg border">
      <h2 className="text-3xl font-bold text-center mb-5 text-indigo-700">
        🔐 Login
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="email"
          placeholder="Email"
          className="w-full p-2 border rounded-md focus:ring-2 focus:ring-indigo-400"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          className="w-full p-2 border rounded-md focus:ring-2 focus:ring-indigo-400"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          disabled={loading}
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white p-2 rounded-md font-semibold transition disabled:opacity-60"
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        {error && <p className="text-red-600 text-sm text-center">{error}</p>}
      </form>

      <div className="text-xs text-slate-500 mt-4 text-center">
        Demo users: <b>admin@example.com</b> / adminpass,{" "}
        <b>user@example.com</b> / userpass
      </div>

      {/* 🆕 Signup Link */}
      <div className="text-center mt-6">
        <p className="text-gray-600">
          Don’t have an account?{" "}
          <button
            onClick={() => nav("/signup")}
            className="text-indigo-600 font-semibold hover:underline"
          >
            Sign up here
          </button>
        </p>
      </div>
    </div>
  );
}
