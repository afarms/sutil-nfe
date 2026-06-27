import React from 'react';
import { Hash } from 'lucide-react';

const InvoiceList = ({ invoices, selectedId, setSelectedInvoice }) => {
  return (
    <div className="space-y-2 max-h-[180px] overflow-y-auto mb-6 pr-2">
      {invoices.map((invoice) => (
        <button
          key={invoice.id}
          onClick={() => setSelectedInvoice(invoice.id)}
          className={`w-full text-left p-3 rounded-2xl border transition-all ${
            selectedId === invoice.id
              ? 'bg-indigo-50 border-indigo-200 shadow-sm'
              : 'bg-slate-50 border-slate-100 hover:bg-white'
          }`}
        >
          <div className="flex justify-between items-center">
            <span className="text-[10px] font-bold text-slate-400">
              #{invoice.numero}
            </span>
            <span className="text-xs font-black text-indigo-600">
              {(invoice.valorOriginal + invoice.incremento).toLocaleString(
                'pt-BR',
                { style: 'currency', currency: 'BRL' }
              )}
            </span>
          </div>
          <p className="text-sm font-bold text-slate-700 truncate">
            {invoice.tomador}
          </p>
        </button>
      ))}
    </div>
  );
};

export default InvoiceList;