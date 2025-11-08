import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [logs, setLogs] = useState([]);
  const [newName, setNewName] = useState("");
  const [reportMsg, setReportMsg] = useState("");
  const [message, setMessage] = useState("");
  const nav = useNavigate();

  // ✅ Load user + logs
  useEffect(() => {
    const load = async () => {
      try {
        const [meRes, logRes] = await Promise.all([
          api.get("/user/me"),
          api.get("/user/logs"),
        ]);
        setUser(meRes.data);
        setLogs(logRes.data || []);
      } catch (err) {
        console.error(err);
        setMessage("Failed to load data");
      }
    };
    load();
  }, []);

  // ✅ Update name
  const updateName = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(`/user/updateName?name=${encodeURIComponent(newName)}`);
      setMessage(res.data.message);
      setUser({ ...user, name: newName });
      setNewName("");
    } catch {
      setMessage("Failed to update name");
    }
  };

  // ✅ Feedback
  const sendFeedback = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(`/user/report?message=${encodeURIComponent(reportMsg)}`);
      setMessage(res.data.message);
      setReportMsg("");
    } catch {
      setMessage("Failed to send feedback");
    }
  };

  if (!user)
    return (
      <Layout>
        <div className="text-center mt-20 text-gray-600">
          Loading your dashboard...
        </div>
      </Layout>
    );

  return (
    <Layout>
      {/* 🔙 Back Button */}
      <button
        onClick={() => nav(-1)}
        className="mb-4 bg-gray-200 text-black px-3 py-1 rounded hover:bg-gray-300 transition"
      >
        ← Back
      </button>

      <motion.div
        className="max-w-5xl mx-auto bg-white shadow-xl rounded-xl p-8"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">
          Welcome, {user.name || user.email.split("@")[0]}
        </h1>

        {/* 👤 Profile Section */}
        <div className="grid md:grid-cols-2 gap-6">
          <div className="bg-indigo-50 rounded-xl p-5">
            <h2 className="text-xl font-semibold mb-3 text-indigo-800">👤 Profile</h2>
            <p><b>Email:</b> {user.email}</p>
            <p><b>Status:</b> {user.active ? "Active" : "Inactive"}</p>

            <form onSubmit={updateName} className="mt-4">
              <input
                type="text"
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                placeholder="Enter new name"
                className="border p-2 rounded w-full mb-2"
              />
              <button className="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">
                Update Name
              </button>
            </form>
          </div>

          {/* 💬 Feedback Section */}
          <div className="bg-gray-50 rounded-xl p-5">
            <h2 className="text-xl font-semibold mb-3 text-gray-800">
              💬 Feedback
            </h2>
            <form onSubmit={sendFeedback}>
              <textarea
                rows="3"
                value={reportMsg}
                onChange={(e) => setReportMsg(e.target.value)}
                placeholder="Write your feedback..."
                className="border p-2 rounded w-full mb-2"
              />
              <button className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700">
                Submit Feedback
              </button>
            </form>
          </div>
        </div>

        {/* 🧾 Logs Section */}
        <div className="mt-8">
          <h2 className="text-xl font-semibold mb-3">🧾 Recent Activity</h2>
          <div className="overflow-y-auto max-h-56 border rounded-lg bg-gray-50 p-3">
            {logs.length === 0 ? (
              <p className="text-gray-500 text-sm">No recent activity found.</p>
            ) : (
              logs.map((log, i) => (
                <p
                  key={i}
                  className="text-sm border-b py-1 text-gray-700"
                >
                  [{new Date(log.timestamp).toLocaleString()}]{" "}
                  <b>{log.type}</b> — {log.action}
                </p>
              ))
            )}
          </div>
        </div>

        {message && (
          <div className="mt-5 text-center text-indigo-700 font-medium">
            {message}
          </div>
        )}
      </motion.div>
    </Layout>
  );
}
