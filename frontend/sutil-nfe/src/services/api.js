const API_BASE = 'http://localhost:8080/api';

export async function processarPdfs() {
  const response = await fetch(`${API_BASE}/notas/processar-pdfs`, {
    method: 'POST',
  });
  if (!response.ok) throw new Error('Erro ao processar PDFs');
  return await response.text(); // backend retorna string "PDFs processados: X"
}


export async function listarAnos(){
    const response = await fetch(`${API_BASE}/notas/anos`);
    if (!response.ok) throw new Error('Erro ao buscar anos disponíveis');
    
    return await response.json();
}


export async function listarNotas(ano) {
    const response = await fetch(`${API_BASE}/notas/${ano}`);
    if (!response.ok) {
        throw new Error('Erro ao buscar notas fiscais');
    }

    const data = await response.json();
    console.log('Dados recebidos da API:', data);
    return data.map(nf => ({
        id: nf.id,
        numero: nf.numero,
        competencia: formatarData(nf.competencia),
        emissao: formatarData(nf.dataEmissao),
        tomador: nf.tomadorNome,
        valorOriginal: Number(nf.valorServico ?? 0),
        incremento: Number(nf.incremento ?? 0),
        splitPJ: Number(nf.splitPj ?? 0),
        descricao: nf.descricaoServico,
    }));
}

export async function atualizarNota(invoice) {
  console.log('Enviando para API:', invoice);
  const response = await fetch(`${API_BASE}/notas/atualizar`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      id: invoice.id,
      numero: invoice.numero,
      competencia: invoice.competencia,
      emissao: invoice.emissao,
      incremento: invoice.incremento,
      splitPj: invoice.splitPJ,
      descricao: invoice.descricao,
    })
  });

  if (!response.ok) throw new Error(`Erro ao atualizar nota fiscal: ${response.status}`);

}

function formatarData(dataIso) {
  if (!dataIso) return '';

  const iso = dataIso.substring(0, 10);
  const [y, m, d] = iso.split('-');
  return `${d}/${m}/${y}`;
}