import React from 'react';
import { LucideIcon } from 'lucide-react';

const StatCard = ({ 
  title, 
  value, 
  icon: Icon, 
  color = 'indigo',
  className = '',
  showCurrency = true,
  compact = false
}) => {
  const colorClasses = {
    indigo: {
      bg: 'bg-indigo-50',
      text: 'text-indigo-600',
      border: 'border-indigo-200',
      icon: 'text-indigo-500'
    },
    emerald: {
      bg: 'bg-emerald-50',
      text: 'text-emerald-600',
      border: 'border-emerald-200',
      icon: 'text-emerald-500'
    },
    slate: {
      bg: 'bg-slate-50',
      text: 'text-slate-600',
      border: 'border-slate-200',
      icon: 'text-slate-500'
    },
    rose: {
      bg: 'bg-rose-50',
      text: 'text-rose-600',
      border: 'border-rose-200',
      icon: 'text-rose-500'
    }
  };

  const colors = colorClasses[color] || colorClasses.indigo;

  const formattedValue = showCurrency && typeof value === 'number'
    ? value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
    : value;

  return (
    <div className={`
      ${colors.bg} ${colors.border}
      ${compact ? 'p-4' : 'p-6'} 
      rounded-2xl border
      flex ${compact ? 'flex-col' : 'flex-row'} items-center gap-4
      ${className}
    `}>
      {Icon && (
        <div className={`p-3 rounded-lg ${colors.bg} ${compact ? 'mb-2' : ''}`}>
          <Icon className={`w-6 h-6 ${colors.icon}`} />
        </div>
      )}
      <div className="flex-1">
        <p className={`text-[10px] font-bold ${colors.text} uppercase mb-1`}>
          {title}
        </p>
        <p className={`text-2xl font-black ${colors.text}`}>
          {formattedValue}
        </p>
      </div>
    </div>
  );
};

export default StatCard;
