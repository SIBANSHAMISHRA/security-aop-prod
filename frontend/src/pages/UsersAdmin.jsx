import React, { useEffect, useState } from "react";
import api from "../api";
import Layout from "../components/Layout";
import { motion } from "framer-motion";
import { Trash2, ShieldCheck, UserX } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function UsersAdmin() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);
  const [refresh, setRefresh] = useState(false);
  const nav = useNavigate();

  const fetchUsers = async () => {
    try {
      const res = await api.get("/admin/users");
      setUsers(res.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load users");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [refresh]);

  const handleDelete = async (email) => {
    if (!window.confirm(`Delete user ${email}?`)) return;
    try {
      await api.post(`/admin/deleteUser?email=${email}`);
      setRefresh(!refresh);
    } catch {
      alert("Failed to delete user");
    }
  };

  const handleToggleActive = async (email, active) => {
    try {
      await api.post(`/admin/setActive?email=${email}&active=${!active}`);
      setRefresh(!refresh);
    } catch {
      alert("Failed to update user status");
    }
  };

  const handlePromote = async (email) => {
    try {
      await api.post(`/admin/updateRole?email=${email}&role=ADMIN`);
      setRefresh(!refresh);
    } catch {
      alert("Failed to promote user");
    }
  };

  return (
    <Layout>
      {/* 🔙 Back Button */}
      <button
        onClick={() => nav(-1)}
        className="mb-4 bg-gray-200 text-black px-3 py-1 rounded hover:bg-gray-300 transition"
      >
        ← Back
      </button>

      <h2 className="text-2xl font-semibold mb-6">User Management</h2>
      {error && <p className="text-red-500">{error}</p>}
      {!users.length && <p>No users found.</p>}

      <motion.table
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="min-w-full bg-white dark:bg-gray-800 shadow-md rounded-lg overflow-hidden"
      >
        <thead className="bg-gray-100 dark:bg-gray-700">
          <tr>
            <th className="py-3 px-4 text-left">Email</th>
            <th className="py-3 px-4 text-center">Role</th>
            <th className="py-3 px-4 text-center">Active</th>
            <th className="py-3 px-4 text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u, i) => (
            <tr
              key={i}
              className="border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 transition"
            >
              <td className="py-3 px-4">{u.email}</td>
              <td className="py-3 px-4 text-center">{u.role}</td>
              <td className="py-3 px-4 text-center">
                {u.active ? (
                  <span className="text-green-500 font-semibold">Active</span>
                ) : (
                  <span className="text-red-400 font-semibold">Inactive</span>
                )}
              </td>
              <td className="py-3 px-4 flex justify-end gap-3">
                <button
                  onClick={() => handlePromote(u.email)}
                  className="text-blue-500 hover:text-blue-700"
                  title="Promote to Admin"
                >
                  <ShieldCheck size={20} />
                </button>
                <button
                  onClick={() => handleToggleActive(u.email, u.active)}
                  className="text-yellow-500 hover:text-yellow-700"
                  title={u.active ? "Deactivate" : "Activate"}
                >
                  <UserX size={20} />
                </button>
                <button
                  onClick={() => handleDelete(u.email)}
                  className="text-red-500 hover:text-red-700"
                  title="Delete User"
                >
                  <Trash2 size={20} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </motion.table>
    </Layout>
  );
}
