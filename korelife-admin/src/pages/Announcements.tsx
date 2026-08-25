import React, { useState } from 'react';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';

const mockAnnouncements = [
  { id: '1', title: 'New Feature: Budget Tracking', type: 'new_feature', target: 'all', active: true, date: '2026-08-15' },
  { id: '2', title: 'Scheduled Maintenance', type: 'warning', target: 'all', active: false, date: '2026-08-10' },
  { id: '3', title: 'Premium Trial Offer', type: 'info', target: 'free', active: true, date: '2026-08-18' },
];

export const Announcements: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const typeColors: Record<string, string> = { new_feature: 'bg-primary-500', warning: 'bg-yellow-500', info: 'bg-blue-500' };

  const columns = [
    { header: 'Title', accessor: 'title' as const },
    { header: 'Type', accessor: (item: any) => <Badge color={typeColors[item.type]}>{item.type.replace('_', ' ')}</Badge> },
    { header: 'Target', accessor: (item: any) => <span className="capitalize">{item.target}</span> },
    { header: 'Status', accessor: (item: any) => <Badge color={item.active ? 'bg-emerald-500' : 'bg-gray-500'}>{item.active ? 'Active' : 'Inactive'}</Badge> },
    { header: 'Date', accessor: 'date' as const },
    { header: 'Actions', accessor: () => <Button variant="secondary" className="py-1 px-3 text-xs">Edit</Button> }
  ];

  return (
    <div className="bg-dark-card rounded-xl border border-dark-border p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-lg font-semibold">Announcements</h3>
        <Button variant="primary" onClick={() => setIsModalOpen(true)}>Create Announcement</Button>
      </div>

      <Table data={mockAnnouncements} columns={columns} keyExtractor={(t) => t.id} />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create Announcement">
         <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Title</label>
            <input type="text" className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Type</label>
              <select className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none">
                <option value="info">Info</option>
                <option value="warning">Warning</option>
                <option value="new_feature">New Feature</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Target Plan</label>
              <select className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none">
                <option value="all">All Users</option>
                <option value="free">Free Users</option>
                <option value="premium">Premium Users</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Content</label>
            <textarea rows={4} className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500"></textarea>
          </div>
          <div className="flex justify-end gap-2 pt-4">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button variant="primary" onClick={() => setIsModalOpen(false)}>Publish</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
