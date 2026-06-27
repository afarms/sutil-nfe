import React from 'react';
import { LayoutDashboard, TrendingUp, Split, Target, ChevronLeft } from 'lucide-react';
import TabButton from '../ui/TabButton';

const Header = ({ activeTab, setActiveTab, anoSelecionado, voltarParaAnos }) => {
  const tabs = [
    { id: 'geral', label: 'Visão Geral', icon: TrendingUp },
    { id: 'divisao', label: 'Divisão PJ/PF', icon: Split },
    { id: 'metas', label: 'Metas 2026', icon: Target }
  ];

  return (
    <header className="max-w-7xl mx-auto mb-8 flex flex-col md:flex-row md:items-end justify-between gap-6 border-b pb-6">
      <div>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight flex items-center gap-2">
          <LayoutDashboard className="text-indigo-600 w-8 h-8" />
          Sutil Finance App
        </h1>
        <p className="text-slate-500 font-medium italic">
          Monitoramento de Faturamento Real-Time
        </p>
        {/* Botão de voltar — só aparece quando há um ano selecionado */}
        {anoSelecionado && (
          <button
            onClick={voltarParaAnos}
            className="mt-2 flex items-center gap-1 text-xs font-bold text-indigo-400 hover:text-indigo-600 transition"
          >
            <ChevronLeft className="w-3 h-3" />
            Voltar para seleção de anos
          </button>
        )}
      </div>

      <nav className="flex bg-slate-200/50 p-1.5 rounded-2xl w-fit">
        {tabs.map(tab => (
          <TabButton
            key={tab.id}
            tab={tab}
            activeTab={activeTab}
            onClick={() => setActiveTab(tab.id)}
          />
        ))}
      </nav>
    </header>
  );
};

export default Header;
