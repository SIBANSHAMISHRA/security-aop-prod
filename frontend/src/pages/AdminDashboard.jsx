import React, { useEffect, useState } from "react";
import api from "../api";
import { motion } from "framer-motion";
import {
  Users,
  UserCheck,
  UserX,
  Shield,
  RefreshCw,
  List,
  Settings,
  MessageCircle
} from "lucide-react";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import Layout from "../components/Layout";
import { useNavigate } from "react-router-dom";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const fetchStats = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      const res = await api.get("/admin/stats", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setStats(res.data);
      setError(null);
    } catch (err) {
      console.error(err);
      setError("Failed to load admin stats");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const COLORS = ["#22c55e", "#f97316", "#3b82f6"];

  if (error)
    return (
      <Layout>
        <div className="text-center text-red-500 font-semibold mt-10">{error}</div>
      </Layout>
    );

  if (!stats)
    return (
      <Layout>
        <div className="text-center text-gray-500 mt-10">Loading dashboard...</div>
      </Layout>
    );

  const chartData = [
    { name: "Active Users", value: stats.activeUsers },
    { name: "Inactive Users", value: stats.inactiveUsers },
    { name: "Admins", value: stats.admins },
  ];

  return (
    <Layout>
      {/* 🔙 Back */}
      <button
        onClick={() => nav(-1)}
        className="mb-4 bg-gray-200 text-black px-3 py-1 rounded hover:bg-gray-300 transition"
      >
        ← Back
      </button>

      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="space-y-8"
      >
        {/* Header */}
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-3xl font-bold text-indigo-700 dark:text-indigo-400 flex items-center gap-2">
            👑 Admin Dashboard
          </h1>
          <button
            onClick={fetchStats}
            disabled={loading}
            className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md shadow-md transition"
          >
            <RefreshCw size={18} />
            {loading ? "Refreshing..." : "Refresh Stats"}
          </button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <StatCard icon={<Users />} title="Total Users" value={stats.totalUsers} color="bg-blue-500" />
          <StatCard icon={<UserCheck />} title="Active Users" value={stats.activeUsers} color="bg-green-500" />
          <StatCard icon={<UserX />} title="Inactive Users" value={stats.inactiveUsers} color="bg-red-500" />
          <StatCard icon={<Shield />} title="Admins" value={stats.admins} color="bg-indigo-500" />
        </div>

        {/* Chart */}
        <div className="p-6 bg-white dark:bg-gray-800 shadow-md rounded-xl">
          <h2 className="text-xl font-semibold mb-4 text-gray-800 dark:text-gray-200">
            📊 User Distribution
          </h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie data={chartData} dataKey="value" nameKey="name" outerRadius={120} label>
                {chartData.map((entry, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Quick Actions */}
        <div className="p-6 bg-white dark:bg-gray-800 shadow-md rounded-xl">
          <h2 className="text-lg font-semibold mb-4 text-gray-800 dark:text-gray-200">
            ⚙️ Quick Actions
          </h2>
          <div className="flex flex-wrap gap-4">
            <ActionButton
              icon={<List size={18} />}
              label="View Logs"
              color="bg-indigo-600 hover:bg-indigo-700"
              link="/admin/logs"
            />
            <ActionButton
              icon={<Users size={18} />}
              label="Manage Users"
              color="bg-green-600 hover:bg-green-700"
              link="/admin/users"
            />
            <ActionButton
              icon={<MessageCircle size={18} />}
              label="User Feedbacks"
              color="bg-purple-600 hover:bg-purple-700"
              link="/admin/feedbacks"
            />
            <ActionButton
              icon={<Settings size={18} />}
              label="System Settings"
              color="bg-yellow-500 hover:bg-yellow-600"
              disabled
            />
          </div>
        </div>
      </motion.div>
    </Layout>
  );
}

function StatCard({ icon, title, value, color }) {
  return (
    <motion.div
      whileHover={{ scale: 1.05 }}
      transition={{ type: "spring", stiffness: 200 }}
      className={`flex items-center gap-3 p-4 rounded-xl shadow-md ${color} text-white`}
    >
      <div className="text-3xl">{icon}</div>
      <div>
        <p className="text-sm">{title}</p>
        <h3 className="text-xl font-bold">{value}</h3>
      </div>
    </motion.div>
  );
}

function ActionButton({ icon, label, color, link, disabled }) {
  return (
    <button
      onClick={() => !disabled && (window.location.href = link)}
      disabled={disabled}
      className={`flex items-center gap-2 px-4 py-2 rounded-md text-white font-medium transition ${
        disabled ? "bg-gray-400 cursor-not-allowed" : color
      }`}
    >
      {icon}
      {label}
    </button>
  );
}
