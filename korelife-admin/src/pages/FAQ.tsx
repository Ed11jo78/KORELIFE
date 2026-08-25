import React, { useState } from 'react';
import { Button } from '../components/ui/Button';
import { Table } from '../components/ui/Table';
import { Modal } from '../components/ui/Modal';
import { Badge } from '../components/ui/Badge';

const mockFAQs = [
  { id: '1', question: 'How to upgrade to Premium?', category: 'Billing', published: true },
  { id: '2', question: 'How to reset my password?', category: 'Account', published: true },
  { id: '3', question: 'What happens if I cancel my subscription?', category: 'Billing', published: false },
];

export const FAQ: React.FC = () => {
  const [faqs, setFaqs] = useState(mockFAQs);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({ question: '', answer: '', category: 'General' });

  const columns = [
    { header: 'Question', accessor: 'question' as const },
    { header: 'Category', accessor: 'category' as const },
    { header: 'Status', accessor: (item: any) => <Badge color={item.published ? 'bg-emerald-500' : 'bg-gray-500'}>{item.published ? 'Published' : 'Draft'}</Badge> },
    { header: 'Actions', accessor: (item: any) => <Button variant="secondary" className="py-1 px-3 text-xs">Edit</Button> }
  ];

  return (
    <div className="bg-dark-card rounded-xl border border-dark-border p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-lg font-semibold">FAQ Management</h3>
        <Button variant="primary" onClick={() => setIsModalOpen(true)}>Add New FAQ</Button>
      </div>

      <Table data={faqs} columns={columns} keyExtractor={(t) => t.id} />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create FAQ Article">
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Question</label>
            <input
              type="text"
              value={formData.question}
              onChange={(e) => setFormData({...formData, question: e.target.value})}
              className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Category</label>
            <select
              value={formData.category}
              onChange={(e) => setFormData({...formData, category: e.target.value})}
              className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500"
            >
              <option>General</option>
              <option>Billing</option>
              <option>Account</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Answer (Markdown supported)</label>
            <textarea
              rows={6}
              value={formData.answer}
              onChange={(e) => setFormData({...formData, answer: e.target.value})}
              className="w-full px-4 py-2 bg-dark-bg border border-dark-border rounded-md text-white outline-none focus:border-primary-500"
            ></textarea>
          </div>
          <div className="flex justify-end gap-2 pt-4">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button variant="primary" onClick={() => setIsModalOpen(false)}>Save Article</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
