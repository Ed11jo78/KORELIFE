import React, { useState } from 'react';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Modal } from '../components/ui/Modal';
import { SearchBar } from '../components/ui/SearchBar';
import { Pagination } from '../components/ui/Pagination';

const mockTickets = Array.from({ length: 12 }).map((_, i) => ({
  id: `TKT-\${1000 + i}`,
  subject: i % 2 === 0 ? 'Cannot access premium features' : 'Billing double charge',
  category: i % 2 === 0 ? 'technical' : 'billing',
  priority: i % 3 === 0 ? 'high' : i % 2 === 0 ? 'medium' : 'low',
  status: i === 0 ? 'open' : i === 1 ? 'in_progress' : 'resolved',
  date: new Date().toLocaleDateString(),
  user: `User \${i + 1}`
}));

export const Tickets: React.FC = () => {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [selectedTicket, setSelectedTicket] = useState<any>(null);
  const [replyText, setReplyText] = useState('');

  const handleReply = () => {
    alert(`Reply sent: ${replyText}`);
    setSelectedTicket(null);
    setReplyText('');
  };

  const priorityColors: Record<string, string> = {
    low: 'bg-gray-500', medium: 'bg-yellow-500', high: 'bg-orange-500', urgent: 'bg-red-500'
  };
  const statusColors: Record<string, string> = {
    open: 'bg-red-500', in_progress: 'bg-blue-500', resolved: 'bg-emerald-500', closed: 'bg-gray-500'
  };

  const columns = [
    { header: 'ID', accessor: 'id' as const },
    { header: 'User', accessor: 'user' as const },
    { header: 'Subject', accessor: 'subject' as const },
    { header: 'Category', accessor: (item: any) => <span className="capitalize">{item.category}</span> },
    { header: 'Priority', accessor: (item: any) => <Badge color={priorityColors[item.priority]}>{item.priority}</Badge> },
    { header: 'Status', accessor: (item: any) => <Badge color={statusColors[item.status]}>{item.status.replace('_', ' ')}</Badge> },
    { header: 'Date', accessor: 'date' as const },
    { header: 'Actions', accessor: (item: any) => <Button variant="secondary" className="py-1 px-3 text-xs" onClick={() => setSelectedTicket(item)}>View & Reply</Button> }
  ];

  return (
    <div className="bg-dark-card rounded-xl border border-dark-border p-6">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-6">
        <h3 className="text-lg font-semibold">Support Tickets</h3>
        <div className="flex gap-2">
          <select className="px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-sm outline-none text-white">
            <option>All Statuses</option>
            <option>Open</option>
            <option>In Progress</option>
            <option>Resolved</option>
          </select>
          <SearchBar value={search} onChange={setSearch} placeholder="Search tickets..." className="w-64" />
        </div>
      </div>

      <Table data={mockTickets} columns={columns} keyExtractor={(t) => t.id} />
      <Pagination currentPage={page} totalPages={3} onPageChange={setPage} />

      <Modal isOpen={!!selectedTicket} onClose={() => setSelectedTicket(null)} title={`Ticket ${selectedTicket?.id}`}>
        {selectedTicket && (
          <div className="space-y-4">
            <div>
              <p className="text-sm text-dark-muted">Subject</p>
              <p className="font-medium">{selectedTicket.subject}</p>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-dark-muted">Status</p>
                <Badge color={statusColors[selectedTicket.status]}>{selectedTicket.status}</Badge>
              </div>
              <div>
                <p className="text-sm text-dark-muted">Priority</p>
                <Badge color={priorityColors[selectedTicket.priority]}>{selectedTicket.priority}</Badge>
              </div>
            </div>
            <div className="border-t border-dark-border pt-4 mt-4">
              <label className="block text-sm font-medium mb-2">Reply to user</label>
              <textarea
                rows={4}
                value={replyText}
                onChange={(e) => setReplyText(e.target.value)}
                className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500 mb-4"
                placeholder="Type your response here..."
              ></textarea>
              <div className="flex justify-end gap-2">
                <Button variant="secondary" onClick={() => setSelectedTicket(null)}>Cancel</Button>
                <Button variant="primary" onClick={handleReply}>Send Reply</Button>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
