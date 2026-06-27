import { useState, useEffect, useMemo, useCallback } from 'react';
import { listarAnos, listarNotas, atualizarNota, processarPdfs } from '../services/api';

export const useInvoices = () => {
  const [anos, setAnos] = useState(null);
  const [anoSelecionado, setAnoSelecionado] = useState(null);
  const [invoices, setInvoices] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [activeTab, setActiveTab] = useState('geral');
  const [metaAnual, setMetaAnual] = useState(81000.00);
  const [viewMode, setViewMode] = useState('individual');

  // Carrega os anos disponíveis na inicialização
  useEffect(() => {
    listarAnos()
      .then(data => {
        if (!data || typeof data !== 'object') {
          console.error('listarAnos retornou valor inválido:', data);
          setAnos({});
          return;
        }
        setAnos(data);
      })
      .catch(err =>{
        console.error('Erro ao carregar anos:', err);
        setAnos({}); // Garante que anos seja um objeto mesmo em caso de erro
      }) 
  }, []);
  
  const handleProcessarPdfs = useCallback(async () => {
    try {
      const resultado = await processarPdfs();
      console.log(resultado);
      // Recarrega os anos após processar
      const anosAtualizados = await listarAnos();
      setAnos(anosAtualizados);
    } catch (err) {
      console.error('Erro ao processar PDFs:', err);
    }
  }, []);

  // Carrega as notas fiscais quando um ano é selecionado
  const selecionarAno = useCallback((ano) => {
    listarNotas(ano)
      .then(notas => {
        setInvoices(notas);
        setAnoSelecionado(ano);
        if (notas.length > 0) setSelectedId(notas[0].id);
      })
      .catch(err => console.error('Erro ao carregar notas:', err));
  }, []);
  
  const voltarParaAnos = useCallback(() => {
    setAnoSelecionado(null);
    setInvoices([]);
    setSelectedId(null);
  }, []);

  const selectedInvoice = useMemo(
    () => invoices.find(inv => inv.id === selectedId) ?? null,
    [invoices, selectedId]
  );

  const handleSplitChange = useCallback((newVal) => {
    setInvoices(prev => prev.map(inv =>
      inv.id === selectedId
        ? { ...inv, splitPJ: parseFloat(newVal) }
        : inv
    ));
  }, [selectedId]);

  const setSelectedInvoice = useCallback((id) => {
    setSelectedId(id);
  }, []);

  const handleIncrementoChange = useCallback((newVal) => {
    setInvoices(prev => prev.map(inv =>
      inv.id === selectedId
        ? { ...inv, incremento: newVal === '' ? '' : parseFloat(newVal)}
        : inv
    ));
  }, [selectedId]);

  const handleDescricaoChange = useCallback((newVal) => {
    setInvoices(prev => prev.map(inv =>
      inv.id === selectedId
      ? { ...inv, descricao: newVal }
      : inv
    ));
  }, [selectedId]);

  const saveInvoice = useCallback(async () => {
    try {
      await atualizarNota(selectedInvoice);
      console.log('Nota atualizada com sucesso!');
    } catch (err) {
      if (err.message.includes('404')) {
        console.error('Nota não encontrada no servidor:', selectedInvoice.id);
      } else {
        console.error('Erro ao salvar nota:', err);
      }
    }
  }, [selectedInvoice]);

  return {
    anos,
    anoSelecionado,
    selecionarAno,
    voltarParaAnos,
    invoices,
    selectedInvoice,
    selectedId,
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
  };
};