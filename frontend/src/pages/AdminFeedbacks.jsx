import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

export default function AdminFeedbacks() {
  const [feedbacks, setFeedbacks] = useState([]);
  const [error, setError] = useState(null);
  const nav = useNavigate();

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await api.get("/admin/feedbacks", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setFeedbacks(res.data);
      } catch (err) {
        console.error("❌ Failed to fetch feedbacks:", err);
        setError("Failed to load feedbacks");
      }
    };
    fetchFeedbacks();
  }, []);

  return (
    <Layout>
      <button
        onClick={() => nav(-1)}
        className="mb-4 bg-gray-200 text-black px-3 py-1 rounded hover:bg-gray-300 transition"
      >
        ← Back
      </button>

      <h2 className="text-2xl font-semibold mb-6">💬 User Feedbacks</h2>
      {error && <p className="text-red-500">{error}</p>}

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="overflow-x-auto bg-white dark:bg-gray-800 shadow-md rounded-lg"
      >
        <table className="min-w-full">
          <thead className="bg-gray-100 dark:bg-gray-700">
            <tr>
              <th className="py-3 px-4 text-left">Email</th>
              <th className="py-3 px-4 text-left">Message</th>
              <th className="py-3 px-4 text-left">Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {feedbacks.length === 0 ? (
              <tr>
                <td colSpan={3} className="text-center py-4 text-gray-500">
                  No feedbacks found.
                </td>
              </tr>
            ) : (
              feedbacks.map((f, i) => (
                <tr
                  key={i}
                  className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 transition"
                >
                  <td className="py-3 px-4">{f.email}</td>
                  <td className="py-3 px-4">{f.message}</td>
                  <td className="py-3 px-4">
                    {new Date(f.timestamp).toLocaleString()}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </motion.div>
    </Layout>
  );
}
