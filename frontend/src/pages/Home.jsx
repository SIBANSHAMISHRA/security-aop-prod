// src/pages/Home.jsx
import React from "react";

export default function Home() {
  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-3xl font-bold text-indigo-700">Security AOP Demo</h1>
      <p className="mt-4 text-slate-600">A demo app showing AOP-based security, audit logging and admin features.</p>

      <div className="mt-8 grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-white rounded-lg shadow">
          <h3 className="font-semibold">Modern UI (Tailwind CDN)</h3>
          <p className="text-sm text-slate-500 mt-2">Admin analytics, user management and audit logs.</p>
        </div>

        <div className="p-6 bg-white rounded-lg shadow">
          <h3 className="font-semibold">Backend-ready</h3>
          <p className="text-sm text-slate-500 mt-2">Connects to your Spring Boot endpoints and stores real users & logs.</p>
        </div>
      </div>
    </div>
  );
}
