import React, { useState } from "react";
import api from "../api";
import { useNavigate } from "react-router-dom";

export default function Signup() {
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const res = await api.post("/auth/signup", form);
      setMessage(res.data.message || "Signup successful! Check your email to verify.");
      setTimeout(() => nav("/login"), 3000);
    } catch (err) {
      console.error("Signup error:", err);
      setError(err.response?.data?.message || "Signup failed. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <h2 className="text-2xl font-bold text-center text-indigo-700 mb-6">
        📝 Create Account
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          name="name"
          type="text"
          placeholder="Full Name"
          value={form.name}
          onChange={handleChange}
          required
          className="w-full p-2 border rounded focus:ring-2 focus:ring-indigo-400"
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
          className="w-full p-2 border rounded focus:ring-2 focus:ring-indigo-400"
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          required
          className="w-full p-2 border rounded focus:ring-2 focus:ring-indigo-400"
        />

        <button
          disabled={loading}
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 rounded transition disabled:opacity-60"
        >
          {loading ? "Signing up..." : "Sign Up"}
        </button>

        {message && <p className="text-green-600 text-center">{message}</p>}
        {error && <p className="text-red-600 text-center">{error}</p>}
      </form>

      <div className="text-center text-sm mt-4 text-gray-500">
        Already have an account?{" "}
        <span
          onClick={() => nav("/login")}
          className="text-indigo-600 hover:underline cursor-pointer"
        >
          Log In
        </span>
      </div>
    </div>
  );
}
