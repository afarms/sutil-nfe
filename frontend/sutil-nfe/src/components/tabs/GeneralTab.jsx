import React from 'react';
import { calculateStats, getMonthlyData } from '../../utils/calculations';
import { useMeiStatus } from '../../hooks/useMeiStatus';
import SidebarConfig from '../layout/SidebarConfig';
import TargetChart from '../charts/TargetChart';
import MonthlyChart from '../charts/MonthlyChart';
import InvoiceStats from '../invoices/InvoiceStats';

const GeneralTab = ({
  invoices,
  selectedInvoice,
  metaAnual,
  setMetaAnual,
  setSelectedInvoice,
  handleSplitChange,
  handleIncrementoChange,
  handleDescricaoChange,
  saveInvoice
}) => {
  const stats = calculateStats(invoices);
  const meiStatus = useMeiStatus(stats.total, metaAnual);
  const monthlyData = getMonthlyData(invoices);
  const targetPieData = [
    { name: 'Faturado', value: stats.total },
    { name: 'Restante', value: Math.max(0, metaAnual - stats.total) }
  ];

  return (
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 animate-in fade-in duration-500">
      <SidebarConfig
        metaAnual={metaAnual}
        setMetaAnual={setMetaAnual}
        meiStatus={meiStatus}
        invoices={invoices}
        selectedId={selectedInvoice?.id}
        setSelectedInvoice={setSelectedInvoice}
        selectedInvoice={selectedInvoice}
        handleSplitChange={handleSplitChange}
        handleIncrementoChange={handleIncrementoChange}
        handleDescricaoChange={handleDescricaoChange}
        saveInvoice={saveInvoice}
      />

      <div className="lg:col-span-8 space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <TargetChart
            targetPieData={targetPieData}
            meiStatus={meiStatus}
            stats={stats}
            metaAnual={metaAnual}
          />
          <MonthlyChart monthlyData={monthlyData} />
        </div>
        <InvoiceStats selectedInvoice={selectedInvoice} />
      </div>
    </div>
  );
};

export default GeneralTab;