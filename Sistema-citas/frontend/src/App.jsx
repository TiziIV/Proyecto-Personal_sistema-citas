import React from 'react';
import { useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import AuthForm from './components/AuthForm';
import Dashboard from './components/Dashboard';

function MainApp() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen bg-slate-100 text-slate-800 dark:bg-gray-900 dark:text-white font-sans transition-colors duration-300">
      {!isAuthenticated ? <AuthForm /> : <Dashboard />}
    </div>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <MainApp />
    </ThemeProvider>
  );
}
