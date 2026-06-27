import React from 'react';
import { Calendar, FileX, FileSearch} from 'lucide-react';

const YearSelector = ({ anos, onSelecionarAno, onProcessarPdfs}) => {
  const entries = Object.entries(anos).sort((a, b) => b[0] - a[0]); // mais recente primeiro

  if (entries.length === 0) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center gap-4">
        <FileX className="w-16 h-16 text-slate-300" />
        <p className="text-slate-400 font-semibold">Nenhum registro encontrado</p>
        <button
          onClick={onProcessarPdfs}
          className="flex items-center gap-2 px-6 py-3 bg-indigo-600 text-white text-sm font-bold rounded-xl hover:bg-indigo-700 transition shadow-lg shadow-indigo-100"
        >
          <FileSearch className="w-4 h-4" />
          Processar PDFs
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-8">
      <div className="mb-8 text-center">
        <h1 className="text-2xl font-black text-slate-700">Selecione o Ano</h1>
        <p className="text-sm text-slate-400 mt-1">Escolha o período para visualizar as notas fiscais</p>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4 w-full max-w-lg">
        {entries.map(([ano, qtd]) => {
          const temRegistros = qtd > 0;
          return (
            <button
              key={ano}
              onClick={() => temRegistros && onSelecionarAno(Number(ano))}
              disabled={!temRegistros}
              className={`
                p-6 rounded-2xl border-2 flex flex-col items-center gap-2 transition-all
                ${temRegistros
                  ? 'bg-white border-slate-200 hover:border-indigo-400 hover:shadow-lg hover:shadow-indigo-100 cursor-pointer'
                  : 'bg-slate-50 border-slate-100 opacity-50 cursor-not-allowed'
                }
              `}
            >
              <Calendar className={`w-6 h-6 ${temRegistros ? 'text-indigo-500' : 'text-slate-300'}`} />
              <span className={`text-2xl font-black ${temRegistros ? 'text-slate-700' : 'text-slate-400'}`}>
                {ano}
              </span>
              <span className={`text-[10px] font-bold uppercase ${temRegistros ? 'text-indigo-400' : 'text-slate-300'}`}>
                {qtd} {qtd === 1 ? 'nota' : 'notas'}
              </span>
            </button>
          );
        })}
      </div>

      {/* Botão sempre visível na tela de seleção */}
      <button
        onClick={onProcessarPdfs}
        className="mt-8 flex items-center gap-2 px-6 py-3 border-2 border-indigo-200 text-indigo-500 text-sm font-bold rounded-xl hover:bg-indigo-50 transition"
      >
        <FileSearch className="w-4 h-4" />
        Processar novos PDFs
      </button>
    </div>
  );
};

export default YearSelector;