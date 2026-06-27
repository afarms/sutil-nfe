import { Edit3 } from 'lucide-react';
import { useEffect, useState } from 'react';

const InvoiceEditForm = ({ 
  selectedInvoice, 
  handleSplitChange,
  handleIncrementoChange,
  handleDescricaoChange,
  saveInvoice
}) => {
  const [showConfirm, setShowConfirm] = useState(false);

  useEffect(() => {
    console.log('Invoice selecionada:', selectedInvoice);
  }, [selectedInvoice]);

  const onSubmit = (e) => {
    e.preventDefault();
    setShowConfirm(true);
  };

  const handleConfirm = () => {
    saveInvoice();
    setShowConfirm(false);
  };

  return (
    <div className="pt-6 border-t border-slate-100 space-y-4">

      {/* Modal de confirmação */}
      {showConfirm && (
        <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 shadow-xl w-80 space-y-4">
            <h3 className="text-sm font-black text-slate-700">Confirmar atualização</h3>
            <p className="text-sm text-slate-500">
              Deseja salvar as alterações da nota: {' '}
              <span className="font-bold text-indigo-600">{selectedInvoice.tomador}</span>?
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowConfirm(false)}
                className="flex-1 py-2 rounded-xl border border-slate-200 text-sm font-bold text-slate-500 hover:bg-slate-50 transition"
              >
                Cancelar
              </button>
              <button
                onClick={handleConfirm}
                className="flex-1 py-2 rounded-xl bg-indigo-600 text-white text-sm font-bold hover:bg-indigo-700 transition"
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}

      <h4 className="text-[10px] font-black text-slate-400 uppercase flex items-center gap-1">
        <Edit3 className="w-3 h-3" /> Editar NF selecionada
      </h4>
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <label className="block text-[10px] font-bold text-slate-400 uppercase mb-1">
            Ajuste / Incremento (R$)
          </label>
          <input
            name="incremento"
            type="number"
            step="0.01"
            className="w-full bg-slate-50 border border-slate-200 rounded-xl p-2.5 text-sm outline-none focus:ring-2 focus:ring-indigo-500"
            placeholder="R$ 0.00"
            value={selectedInvoice.incremento ?? 0}
            onChange={(e) => handleIncrementoChange(e.target.value)}
          />
        </div>
        <div>
          <div className="flex justify-between text-[10px] font-black text-slate-400 uppercase mb-1">
            <span>Regra Empresa (PJ)</span>
            <span className="text-indigo-600">{selectedInvoice.splitPJ}%</span>
          </div>
          <input
            type="range"
            min="0"
            max="100"
            step="5"
            value={selectedInvoice.splitPJ}
            onChange={(e) => handleSplitChange(e.target.value)}
            className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
          />
        </div>
        <textarea
          name="descricao"
          className="w-full bg-slate-50 border border-slate-200 rounded-xl p-2.5 text-sm outline-none focus:ring-2 focus:ring-indigo-500"
          rows="2"
          placeholder="Nova descrição..."
          value={selectedInvoice.descricao}
          onChange={(e) => handleDescricaoChange(e.target.value)}
        />
        <button
          type="submit"
          className="w-full bg-indigo-600 text-white font-bold py-3 rounded-xl transition hover:bg-indigo-700 shadow-lg shadow-indigo-100"
        >
          Atualizar Atributos
        </button>
      </form>
    </div>
  );
};

export default InvoiceEditForm;