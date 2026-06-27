import { useInvoices } from './hooks/useInvoices';
import Header from './components/layout/Header';
import GeneralTab from './components/tabs/GeneralTab';
import DivisionTab from './components/tabs/DivisionTab';
import GoalsTab from './components/tabs/GoalsTab';
import YearSelector from './components/tabs/YearSelector';
import './App.css';

const App = () => {
  const {
    anos,
    anoSelecionado,
    selecionarAno,
    voltarParaAnos,
    invoices,
    selectedInvoice,
    activeTab,
    metaAnual,
    viewMode,
    setSelectedInvoice,
    setActiveTab,
    setMetaAnual,
    setViewMode,
    handleSplitChange,
    handleIncrementoChange,
    handleDescricaoChange,
    saveInvoice,
    handleProcessarPdfs
  } = useInvoices();

  // Ainda carregando os anos
  if (anos === null) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="p-8 text-slate-600 font-semibold">Carregando...</div>
      </div>
    );
  }

  // Nenhum ano selecionado → tela de seleção
  // {} = carregou mas veio vazio → YearSelector vai mostrar a mensagem de vazio
  if (!anoSelecionado) {
    return (
      <YearSelector 
        anos={anos} 
        onSelecionarAno={selecionarAno} 
        onProcessarPdfs={handleProcessarPdfs}
      />
    );
  }

  const renderTabContent = () => {
    switch (activeTab) {
      case 'geral':
        return (
          <GeneralTab
            invoices={invoices}
            selectedInvoice={selectedInvoice}
            metaAnual={metaAnual}
            setMetaAnual={setMetaAnual}
            setSelectedInvoice={setSelectedInvoice}
            handleSplitChange={handleSplitChange}
            handleIncrementoChange={handleIncrementoChange}
            handleDescricaoChange={handleDescricaoChange}
            saveInvoice={saveInvoice}
          />
        );
      case 'divisao':
        return (
          <DivisionTab
            invoices={invoices}
            selectedInvoice={selectedInvoice}
            viewMode={viewMode}
            setViewMode={setViewMode}
          />
        );
      case 'metas':
        return <GoalsTab metaAnual={metaAnual} invoices={invoices} />;
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 font-sans p-4 md:p-8">
      <Header 
        activeTab={activeTab} 
        setActiveTab={setActiveTab} 
        anoSelecionado={anoSelecionado}
        voltarParaAnos={voltarParaAnos}
      />
      <main className="max-w-7xl mx-auto">
        {renderTabContent()}
      </main>
    </div>
  );
};

export default App;