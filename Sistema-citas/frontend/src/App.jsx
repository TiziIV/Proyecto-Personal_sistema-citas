import React from 'react';
import { useAuth } from './context/AuthContext';
import AuthForm from './components/AuthForm';
import Dashboard from './components/Dashboard';

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="bg-gray-900 min-h-screen text-white font-sans">
      {!isAuthenticated ? <AuthForm /> : <Dashboard />}
    </div>
  );
}
