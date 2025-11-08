import React, { useState, useEffect } from "react";
import { Sun, Moon } from "lucide-react";
import Nav from "./Nav";

export default function Layout({ children }) {
  const [dark, setDark] = useState(false);

  useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  return (
    <div
      className={`flex flex-col min-h-screen transition-colors duration-500 ${
        dark ? "bg-gray-900 text-white" : "bg-gray-50 text-gray-900"
      }`}
    >
      {/* Header Bar */}
      <div className="flex justify-between items-center p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-inherit z-50">
        <h1 className="text-xl font-bold text-brand">Security AOP Admin</h1>
        <button
          onClick={() => setDark(!dark)}
          className="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition"
          title={dark ? "Switch to light mode" : "Switch to dark mode"}
        >
          {dark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
        </button>
      </div>

      {/* Navigation Bar */}
      <Nav />

      {/* Main Content Area */}
      <main className="flex-1 overflow-y-auto p-6 md:p-10">
        {children}
      </main>
    </div>
  );
}
