import React from 'react';
import {
  AreaChart,
  Area,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { Target } from 'lucide-react';
import { calculateStats } from '../../utils/calculations';

const GoalsTab = ({ metaAnual, invoices }) => {
  const stats = calculateStats(invoices);

  const planningData = [
    { mes: 'Planejado', valor: metaAnual },
    { mes: 'Realizado', valor: stats.total },
  ];

  const difference = metaAnual - stats.total;
  const projection = stats.total * 6; // Projeção para 12 meses baseada na média

  return (
    <div className="animate-in slide-in-from-bottom-4 duration-500 max-w-4xl mx-auto">
      <div className="bg-white rounded-3xl shadow-sm border border-slate-100 p-8">
        <h2 className="text-2xl font-black text-slate-900 mb-8 flex items-center gap-3">
          <Target className="text-indigo-600" /> Planejamento 2026
        </h2>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={planningData}>
              <CartesianGrid
                strokeDasharray="3 3"
                vertical={false}
                stroke="#f1f5f9"
              />
              <XAxis
                dataKey="mes"
                axisLine={false}
                tickLine={false}
                tick={{ fontWeight: 700, fontSize: 14 }}
              />
              <YAxis hide />
              <Tooltip
                formatter={(value) =>
                  value.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL',
                  })
                }
              />
              <Area
                type="monotone"
                dataKey="valor"
                stroke="#6366f1"
                fill="#6366f1"
                fillOpacity={0.1}
                strokeWidth={4}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8 pt-8 border-t">
          <div className="text-center">
            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">
              Alcance Atual
            </p>
            <p className="text-3xl font-black text-indigo-600">
              {((stats.total / metaAnual) * 100).toFixed(1)}%
            </p>
          </div>
          <div className="text-center">
            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">
              Diferença p/ Meta
            </p>
            <p
              className={`text-3xl font-black ${
                stats.total > metaAnual ? 'text-rose-500' : 'text-slate-700'
              }`}
            >
              {Math.abs(difference).toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL',
              })}
            </p>
          </div>
          <div className="text-center">
            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">
              Projeção 12 Meses
            </p>
            <p className="text-3xl font-black text-emerald-500">
              {projection.toLocaleString('pt-BR', {
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

export default GoalsTab;
