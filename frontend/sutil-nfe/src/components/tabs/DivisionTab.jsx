import React from 'react';
import { FileText, Briefcase } from 'lucide-react';
import RevenueSplitChart from '../charts/RevenueSplitChart';
import { calculateStats } from '../../utils/calculations';

const DivisionTab = ({ invoices, selectedInvoice, viewMode, setViewMode }) => {
  const stats = calculateStats(invoices);

  const splitData =
    viewMode === 'individual'
      ? [
          {
            name: 'PJ',
            value:
              (selectedInvoice.valorOriginal + selectedInvoice.incremento) *
              (selectedInvoice.splitPJ / 100),
          },
          {
            name: 'PF',
            value:
              (selectedInvoice.valorOriginal + selectedInvoice.incremento) *
              ((100 - selectedInvoice.splitPJ) / 100),
          },
        ]
      : [
          { name: 'PJ Total', value: stats.pj },
          { name: 'PF Total', value: stats.pf },
        ];

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 animate-in slide-in-from-right-4 duration-500">
      <div className="lg:col-span-2 space-y-6">
        <RevenueSplitChart
          data={splitData}
          viewMode={viewMode}
          stats={stats}
          selectedInvoice={selectedInvoice}
        />
      </div>

      <div className="bg-white p-6 rounded-3xl border border-slate-100 space-y-6">
        {viewMode === 'individual' ? (
          <>
            <h3 className="font-black text-slate-800 text-sm uppercase tracking-widest border-b pb-2 flex items-center gap-2">
              <FileText className="w-4 h-4 text-indigo-500" /> Atributos da Nota
            </h3>
            <div className="space-y-6">
              <div>
                <p className="text-[10px] font-black text-slate-400 uppercase tracking-tighter mb-1">
                  Tomador do Serviço
                </p>
                <p className="text-sm font-bold text-slate-700 uppercase leading-tight">
                  {selectedInvoice.tomador}
                </p>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                    Nº
                  </p>
                  <p className="text-xs font-bold text-slate-700">
                    #{selectedInvoice.numero}
                  </p>
                </div>
                <div>
                  <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                    Data
                  </p>
                  <p className="text-xs font-bold text-slate-700">
                    {selectedInvoice.competencia}
                  </p>
                </div>
              </div>
              <div>
                <p className="text-[10px] font-black text-slate-400 uppercase mb-1">
                  Descrição
                </p>
                <p className="text-xs text-slate-600 italic leading-relaxed border-l-2 border-indigo-100 pl-3">
                  "{selectedInvoice.descricao}"
                </p>
              </div>
            </div>
          </>
        ) : (
          <>
            <h3 className="font-black text-slate-800 text-sm uppercase tracking-widest border-b pb-2 flex items-center gap-2">
              <Briefcase className="w-4 h-4 text-indigo-500" /> Resumo do
              Período
            </h3>
            <div className="space-y-6">
              <div className="flex justify-between items-center bg-slate-50 p-4 rounded-2xl">
                <span className="text-xs font-bold text-slate-500">
                  Notas Processadas
                </span>
                <span className="text-xl font-black text-indigo-600">
                  {invoices.length}
                </span>
              </div>
              <div className="p-4 bg-indigo-50 rounded-2xl">
                <p className="text-[10px] font-bold text-indigo-400 uppercase mb-1">
                  Faturamento Médio / Nota
                </p>
                <p className="text-xl font-black text-indigo-700">
                  {(invoices.length > 0 ? stats.total / invoices.length : 0).toLocaleString(
                    'pt-BR',
                    { style: 'currency', currency: 'BRL' }
                  )}
                </p>
              </div>
              <div className="p-4 bg-emerald-50 rounded-2xl border border-emerald-100">
                <p className="text-[10px] font-bold text-emerald-500 uppercase mb-1">
                  Total Acumulado:
                </p>
                <p className="text-base font-black text-emerald-700">
                  {(invoices.length > 0 ? stats.total : 0).toLocaleString(
                    'pt-BR',
                    { style: 'currency', currency: 'BRL' }
                  )}
                </p>
              </div>
            </div>
          </>
        )}

        <div className="pt-6 border-t border-slate-100">
          <div className="flex bg-slate-100 p-1 rounded-xl">
            <button
              onClick={() => setViewMode('individual')}
              className={`px-4 py-1.5 text-[10px] font-black uppercase rounded-lg transition ${
                viewMode === 'individual'
                  ? 'bg-white text-indigo-600 shadow-sm'
                  : 'text-slate-400'
              }`}
            >
              Individual
            </button>
            <button
              onClick={() => setViewMode('agrupado')}
              className={`px-4 py-1.5 text-[10px] font-black uppercase rounded-lg transition ${
                viewMode === 'agrupado'
                  ? 'bg-white text-indigo-600 shadow-sm'
                  : 'text-slate-400'
              }`}
            >
              Agrupado
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DivisionTab;
