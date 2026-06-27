import React from 'react';
import { Hash, TrendingUp } from 'lucide-react';

const InvoiceStats = ({ selectedInvoice }) => {
  if (!selectedInvoice) return null;

  const totalValue = selectedInvoice.valorOriginal + selectedInvoice.incremento;

  return (
    <div className="bg-slate-900 rounded-3xl p-8 text-white relative overflow-hidden flex flex-col md:flex-row justify-between items-center gap-6">
      <div className="relative z-10 space-y-2">
        <div className="flex items-center gap-2">
          <Hash className="w-3 h-3 text-indigo-400" />
          <h3 className="text-xs font-black text-indigo-400 uppercase tracking-widest">
            Atualmente em análise
          </h3>
        </div>
        <p className="text-2xl font-black">{selectedInvoice.tomador}</p>
        <div className="flex gap-4 mt-6">
          <div className="bg-white/5 px-4 py-2 rounded-2xl border border-white/10 flex flex-col min-w-[100px]">
            <span className="text-[10px] text-indigo-300 font-bold uppercase block">
              Empresa (PJ)
            </span>
            <span className="text-xl font-black text-indigo-400">
              {selectedInvoice.splitPJ}%
            </span>
          </div>
          <div className="bg-white/5 px-4 py-2 rounded-2xl border border-white/10 flex flex-col min-w-[100px]">
            <span className="text-[10px] text-slate-400 font-bold uppercase block">
              Pessoal (PF)
            </span>
            <span className="text-xl font-black text-slate-300">
              {100 - selectedInvoice.splitPJ}%
            </span>
          </div>
        </div>
      </div>
      <div className="relative z-10 bg-white/10 backdrop-blur-md p-8 rounded-[2.5rem] border border-white/10 text-center min-w-[240px]">
        <p className="text-[10px] font-bold text-indigo-200 uppercase mb-1">
          Valor Final Acordado
        </p>
        <p className="text-4xl font-black">
          {totalValue.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL',
          })}
        </p>
      </div>
      <div className="absolute top-0 right-0 p-4 opacity-5 pointer-events-none">
        <TrendingUp className="w-48 h-48" />
      </div>
    </div>
  );
};

export default InvoiceStats;
