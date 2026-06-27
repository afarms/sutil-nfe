import React from 'react';
import { Target, AlertTriangle } from 'lucide-react';
import InvoiceList from '../invoices/InvoiceList';
import InvoiceEditForm from '../invoices/InvoiceEditForm';
import { DEFAULT_VALUES } from '../../constants/defaultValues';

const SidebarConfig = ({
  metaAnual,
  setMetaAnual,
  meiStatus,
  invoices,
  selectedId,
  setSelectedInvoice,
  selectedInvoice,
  handleSplitChange,
  handleIncrementoChange,
  handleDescricaoChange,
  saveInvoice
}) => {
  return (
    <div className="lg:col-span-4 space-y-6">
      {/* Seção da Meta Anual */}
      <div className="bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="bg-indigo-100 p-2 rounded-lg">
              <Target className="w-5 h-5 text-indigo-600" />
            </div>
            <h2 className="text-sm font-black text-slate-400 uppercase tracking-widest">
              Meta Anual Alvo
            </h2>
          </div>
          <span
            className={`px-2 py-1 rounded-md text-[9px] font-black uppercase ${meiStatus.bgColor} ${meiStatus.color} border ${meiStatus.borderColor}`}
          >
            {meiStatus.label}
          </span>
        </div>

        <div className="space-y-4">
          <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 font-bold text-slate-400 text-sm">
              R$
            </span>
            <input
              type="number"
              value={metaAnual}
              onChange={(e) => setMetaAnual(Number(e.target.value))}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl py-3 pl-10 pr-4 font-black text-xl text-indigo-600 focus:ring-2 focus:ring-indigo-500 outline-none"
            />
          </div>

          <div className={`p-4 rounded-2xl border ${meiStatus.bgColor} ${meiStatus.borderColor} flex items-center justify-between shadow-inner`}>
            <div className="flex items-center gap-2">
              <AlertTriangle className={`w-4 h-4 ${meiStatus.color}`} />
              <span className={`text-[10px] font-black uppercase tracking-tight ${meiStatus.color}`}>
                {meiStatus.msg}
              </span>
            </div>
            <span className={`text-sm font-black ${meiStatus.color}`}>
              {meiStatus.value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </span>
          </div>

          <input
            type="range"
            min={DEFAULT_VALUES.TARGET_RANGE.MIN}
            max={DEFAULT_VALUES.TARGET_RANGE.MAX}
            step={DEFAULT_VALUES.TARGET_RANGE.STEP}
            value={metaAnual}
            onChange={(e) => setMetaAnual(Number(e.target.value))}
            className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
          />
        </div>
      </div>

      {/* Seção de Edição de Notas */}
      <div className="bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
        <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest mb-4">
          Selecione para Editar
        </h3>
        
        <InvoiceList
          invoices={invoices}
          selectedId={selectedId}
          setSelectedInvoice={setSelectedInvoice}
        />

        {selectedInvoice && (
          <InvoiceEditForm
            selectedInvoice={selectedInvoice}
            handleSplitChange={handleSplitChange}
            handleIncrementoChange={handleIncrementoChange}
            handleDescricaoChange={handleDescricaoChange}
            saveInvoice={saveInvoice}
          />
        )}
      </div>
    </div>
  );
};

export default SidebarConfig;