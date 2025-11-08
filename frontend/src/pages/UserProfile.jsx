import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";

export default function UserProfile() {
  const [profile, setProfile] = useState(null);
  const [name, setName] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await api.get("/api/user/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setProfile(res.data);
        setName(res.data.name || "");
      } catch (err) {
        console.error(err);
      }
    };
    fetchProfile();
  }, []);

  const handleUpdate = async () => {
    setLoading(true);
    setMessage("");
    try {
      const token = localStorage.getItem("token");
      const res = await api.post(
        "/api/user/update",
        { name },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMessage(res.data.message || "Profile updated successfully");
    } catch (err) {
      setMessage("Failed to update profile");
    } finally {
      setLoading(false);
    }
  };

  if (!profile) return <Layout><p>Loading profile...</p></Layout>;

  return (
    <Layout>
      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-xl mx-auto bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md"
      >
        <h2 className="text-2xl font-semibold mb-4">My Profile</h2>
        <p><strong>Email:</strong> {profile.email}</p>
        <p><strong>Role:</strong> {profile.role}</p>

        <div className="mt-6">
          <label className="block text-sm font-medium mb-2">Name</label>
          <input
            className="w-full px-3 py-2 border rounded-md dark:bg-gray-700"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <button
          onClick={handleUpdate}
          disabled={loading}
          className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          {loading ? "Updating..." : "Update Profile"}
        </button>

        {message && <p className="mt-4 text-green-500">{message}</p>}
      </motion.div>
    </Layout>
  );
}
