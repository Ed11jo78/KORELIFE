import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export const Pagination: React.FC<PaginationProps> = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2 mt-6">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        className="p-2 rounded-md bg-dark-bg border border-dark-border text-dark-text disabled:opacity-50 hover:bg-dark-border transition-colors"
      >
        <ChevronLeft size={18} />
      </button>
      <span className="text-sm text-dark-muted">Page {currentPage} of {totalPages}</span>
      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        className="p-2 rounded-md bg-dark-bg border border-dark-border text-dark-text disabled:opacity-50 hover:bg-dark-border transition-colors"
      >
        <ChevronRight size={18} />
      </button>
    </div>
  );
};
