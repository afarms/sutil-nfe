import React from 'react';

const Card = ({ children, className = '', padding = 'p-6', rounded = 'rounded-3xl', shadow = true, border = true }) => {
  return (
    <div className={`
      bg-white
      ${rounded}
      ${shadow ? 'shadow-sm' : ''}
      ${border ? 'border border-slate-100' : ''}
      ${padding}
      ${className}
    `}>
      {children}
    </div>
  );
};

export const CardHeader = ({ children, className = '', border = false }) => {
  return (
    <div className={`
      ${border ? 'border-b border-slate-100 pb-4 mb-4' : ''}
      ${className}
    `}>
      {children}
    </div>
  );
};

export const CardTitle = ({ children, className = '' }) => {
  return (
    <h3 className={`text-sm font-black text-slate-400 uppercase tracking-widest ${className}`}>
      {children}
    </h3>
  );
};

export default Card;
