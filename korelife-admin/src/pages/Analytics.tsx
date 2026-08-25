import React from 'react';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

const growthData = [
  { date: 'Mon', users: 400 },
  { date: 'Tue', users: 300 },
  { date: 'Wed', users: 550 },
  { date: 'Thu', users: 480 },
  { date: 'Fri', users: 600 },
  { date: 'Sat', users: 800 },
  { date: 'Sun', users: 950 },
];

const planData = [
  { name: 'Free', value: 8000 },
  { name: 'Premium', value: 2500 },
  { name: 'Family', value: 1200 },
  { name: 'Team', value: 300 },
];

const ticketData = [
  { category: 'Billing', count: 120 },
  { category: 'Technical', count: 250 },
  { category: 'General', count: 80 },
  { category: 'Feedback', count: 45 },
];

const COLORS = ['#94a3b8', '#10b981', '#3b82f6', '#8b5cf6'];

export const Analytics: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* User Growth */}
        <div className="bg-dark-card p-6 rounded-xl border border-dark-border h-96">
          <h3 className="text-lg font-semibold mb-6">New Users Registration (Last 7 days)</h3>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={growthData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
              <XAxis dataKey="date" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155' }} />
              <Line type="monotone" dataKey="users" stroke="#6366f1" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Plan Distribution */}
        <div className="bg-dark-card p-6 rounded-xl border border-dark-border h-96">
          <h3 className="text-lg font-semibold mb-6">Plan Distribution</h3>
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={planData}
                cx="50%"
                cy="50%"
                innerRadius={80}
                outerRadius={120}
                paddingAngle={5}
                dataKey="value"
              >
                {planData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155' }} />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Tickets by Category */}
        <div className="bg-dark-card p-6 rounded-xl border border-dark-border h-96 lg:col-span-2">
          <h3 className="text-lg font-semibold mb-6">Support Tickets by Category</h3>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={ticketData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
              <XAxis dataKey="category" stroke="#94a3b8" />
              <YAxis stroke="#94a3b8" />
              <Tooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155' }} cursor={{ fill: '#334155', opacity: 0.4 }} />
              <Bar dataKey="count" fill="#f59e0b" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};
