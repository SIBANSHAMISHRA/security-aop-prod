import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";

export default function UserLogs() {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await api.get("/api/user/logs", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setLogs(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchLogs();
  }, []);

  return (
    <Layout>
      <h2 className="text-2xl font-semibold mb-6">My Activity Logs</h2>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="overflow-x-auto bg-white dark:bg-gray-800 shadow-md rounded-lg"
      >
        <table className="min-w-full">
          <thead className="bg-gray-100 dark:bg-gray-700">
            <tr>
              <th className="py-3 px-4 text-left">Timestamp</th>
              <th className="py-3 px-4 text-left">Event</th>
              <th className="py-3 px-4 text-left">Severity</th>
            </tr>
          </thead>
          <tbody>
            {logs.length === 0 ? (
              <tr>
                <td colSpan={3} className="text-center py-4 text-gray-500">
                  No logs yet.
                </td>
              </tr>
            ) : (
              logs.map((log, i) => (
                <tr key={i} className="border-b border-gray-200 dark:border-gray-700">
                  <td className="py-3 px-4">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="py-3 px-4">{log.event}</td>
                  <td className="py-3 px-4">{log.severity}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </motion.div>
    </Layout>
  );
}
