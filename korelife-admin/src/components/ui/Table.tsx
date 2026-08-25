import React from 'react';

interface Column<T> {
  header: string;
  accessor: keyof T | ((item: T) => React.ReactNode);
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (item: T) => string;
}

export function Table<T>({ columns, data, keyExtractor }: TableProps<T>) {
  return (
    <div className="overflow-x-auto w-full">
      <table className="w-full text-left text-sm">
        <thead className="bg-dark-bg text-dark-muted border-b border-dark-border">
          <tr>
            {columns.map((col, i) => (
              <th key={i} className="px-6 py-4 font-medium whitespace-nowrap">{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-dark-border">
          {data.length > 0 ? (
            data.map((item) => (
              <tr key={keyExtractor(item)} className="hover:bg-dark-bg/50 transition-colors">
                {columns.map((col, j) => (
                  <td key={j} className="px-6 py-4">
                    {typeof col.accessor === 'function' ? col.accessor(item) : (item[col.accessor] as React.ReactNode)}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={columns.length} className="px-6 py-8 text-center text-dark-muted">
                No data available.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
