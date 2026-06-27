import React from 'react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import { Building2, UserCheck } from 'lucide-react';

const RevenueSplitChart = ({ data, viewMode, stats, selectedInvoice }) => {
  const colors = ['#6366f1', '#94a3b8'];

  return (
    <div className="bg-white rounded-3xl shadow-sm border border-slate-100 p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h2 className="text-xl font-black text-slate-800 tracking-tight uppercase">
            Partilha de Receita
          </h2>
          <p className="text-xs text-slate-400 font-bold uppercase tracking-widest">
            {viewMode === 'individual'
              ? `Nota Fiscal Nº ${selectedInvoice?.numero}`
              : 'Acumulado de Todas as Notas'}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                innerRadius={70}
                outerRadius={95}
                paddingAngle={8}
                dataKey="value"
              >
                {data.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={colors[index]} stroke="none" />
                ))}
              </Pie>
              <Tooltip
                formatter={(value) =>
                  value.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })
                }
              />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="space-y-4">
          <div className="p-6 rounded-2xl border-l-4 border-indigo-500 bg-indigo-50/30">
            <div className="flex items-center gap-2 mb-1">
              <Building2 className="w-4 h-4 text-indigo-500" />
              <p className="text-[10px] font-black text-indigo-400 uppercase">
                Empresa (PJ)
              </p>
            </div>
            <p className="text-3xl font-black text-indigo-700">
              {viewMode === 'individual'
                ? (
                    (selectedInvoice.valorOriginal + selectedInvoice.incremento) *
                    (selectedInvoice.splitPJ / 100)
                  ).toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })
                : stats.pj.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })}
            </p>
          </div>

          <div className="p-6 rounded-2xl border-l-4 border-slate-400 bg-slate-100/50">
            <div className="flex items-center gap-2 mb-1">
              <UserCheck className="w-4 h-4 text-slate-500" />
              <p className="text-[10px] font-black text-slate-400 uppercase">
                Pessoal (PF)
              </p>
            </div>
            <p className="text-3xl font-black text-slate-700">
              {viewMode === 'individual'
                ? (
                    (selectedInvoice.valorOriginal + selectedInvoice.incremento) *
                    ((100 - selectedInvoice.splitPJ) / 100)
                  ).toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })
                : stats.pf.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RevenueSplitChart;