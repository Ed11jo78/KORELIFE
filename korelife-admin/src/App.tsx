import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import { Login } from './pages/Login';
import { Layout } from './components/Layout';
import { Dashboard } from './pages/Dashboard';
import { Users } from './pages/Users';
import { Transactions } from './pages/Transactions';
import { RatesControl } from './pages/RatesControl';
import { Tickets } from './pages/Tickets';
import { FAQ } from './pages/FAQ';
import { Announcements } from './pages/Announcements';
import { Analytics } from './pages/Analytics';
import { AppSimulator } from './pages/AppSimulator';
import { Toaster } from 'react-hot-toast';

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
};

function App() {
  return (
    <Router>
      <Toaster
        position="top-right"
        toastOptions={{
          className: 'bg-dark-card text-white border border-dark-border shadow-xl',
          duration: 3000
        }}
      />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route index element={<Dashboard />} />
          <Route path="users" element={<Users />} />
          <Route path="transactions" element={<Transactions />} />
          <Route path="rates" element={<RatesControl />} />
          <Route path="tickets" element={<Tickets />} />
          <Route path="faq" element={<FAQ />} />
          <Route path="announcements" element={<Announcements />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="simulator" element={<AppSimulator />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
