import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { Download } from "lucide-react";

export default function AdminLogs() {
  const [logs, setLogs] = useState([]);
  const [error, setError] = useState(null);
  const nav = useNavigate();

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await api.get("/admin/securityLogs", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setLogs(res.data);
      } catch (err) {
        console.error("❌ Failed to fetch logs:", err);
        setError("Failed to load security logs");
      }
    };
    fetchLogs();
  }, []);

  const exportLogs = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await api.get("/admin/exportLogs", {
        headers: { Authorization: `Bearer ${token}` },
        responseType: "blob",
      });

      const blob = new Blob([res.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "security_logs.xlsx";
      link.click();
    } catch (err) {
      alert("Failed to export logs");
      console.error(err);
    }
  };

  return (
    <Layout>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">🔒 Security Logs</h2>
        <div className="flex gap-3">
          <button
            onClick={exportLogs}
            className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md shadow"
          >
            <Download size={18} /> Export Excel
          </button>
          <button
            onClick={() => nav(-1)}
            className="bg-gray-300 hover:bg-gray-400 text-black px-3 py-2 rounded-md"
          >
            ← Back
          </button>
        </div>
      </div>

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
              <th className="py-3 px-4 text-left">Timestamp</th>
              <th className="py-3 px-4 text-left">User</th>
              <th className="py-3 px-4 text-left">Action</th>
              <th className="py-3 px-4 text-left">Type</th>
            </tr>
          </thead>
          <tbody>
            {logs.length === 0 ? (
              <tr>
                <td colSpan={4} className="text-center py-4 text-gray-500">
                  No security logs found yet.
                </td>
              </tr>
            ) : (
              logs.map((log, i) => (
                <tr
                  key={i}
                  className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 transition"
                >
                  <td className="py-3 px-4">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="py-3 px-4">{log.email || "N/A"}</td>
                  <td className="py-3 px-4">{log.action}</td>
                  <td className="py-3 px-4">
                    <span
                      className={`px-2 py-1 rounded text-xs font-semibold ${
                        log.type === "ERROR"
                          ? "bg-red-500 text-white"
                          : log.type === "ADMIN_ACTION"
                          ? "bg-blue-500 text-white"
                          : "bg-green-500 text-white"
                      }`}
                    >
                      {log.type || "INFO"}
                    </span>
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
