import React from 'react';

const TabButton = ({ tab, activeTab, onClick }) => {
  const Icon = tab.icon;

  return (
    <button
      onClick={onClick}
      className={`px-5 py-2.5 rounded-xl font-bold text-sm transition-all flex items-center gap-2 ${
        activeTab === tab.id
          ? 'bg-white text-indigo-600 shadow-sm'
          : 'text-slate-500 hover:text-slate-700'
      }`}
    >
      <Icon className="w-4 h-4" /> {tab.label}
    </button>
  );
};

export default TabButton;