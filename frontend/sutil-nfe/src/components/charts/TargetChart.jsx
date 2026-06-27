import React from 'react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import { AlertTriangle } from 'lucide-react';

const TargetChart = ({ targetPieData, meiStatus, stats, metaAnual }) => {
  return (
    <div className="bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
      <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest mb-6">
        ATINGIMENTO DA META MEI
      </h3>
      <div className="h-64 relative">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={targetPieData}
              innerRadius={70}
              outerRadius={90}
              paddingAngle={5}
              dataKey="value"
            >
              <Cell fill={meiStatus.chart} stroke="none" />
              <Cell fill="#f1f5f9" stroke="none" />
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
        <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <span
            className={`text-4xl font-black tracking-tighter ${meiStatus.color}`}
          >
            {((stats.total / metaAnual) * 100).toFixed(0)}%
          </span>
          <span className="text-[10px] font-bold text-slate-400 uppercase">
            da Meta
          </span>
        </div>
      </div>
      <div
        className={`mt-4 p-4 rounded-2xl border ${meiStatus.bgColor} ${meiStatus.borderColor} flex items-center justify-between shadow-inner`}
      >
        <div className="flex items-center gap-2">
          <AlertTriangle className={`w-4 h-4 ${meiStatus.color}`} />
          <span
            className={`text-[10px] font-black uppercase tracking-tight ${meiStatus.color}`}
          >
            {meiStatus.msg}
          </span>
        </div>
        <span className={`text-sm font-black ${meiStatus.color}`}>
          {meiStatus.value.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL',
          })}
        </span>
      </div>
    </div>
  );
};

export default TargetChart;
