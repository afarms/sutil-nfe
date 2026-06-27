export const calculateStats = (invoices) => {
  if (!invoices.length) {
    return { total: 0, pj: 0, pf: 0 };
  }

  return invoices.reduce((acc, inv) => {
    const total = (inv.valorOriginal ?? 0) + (Number(inv.incremento) || 0); // ← fix aqui
    const split = inv.splitPJ ?? 60;

    acc.total += total;
    acc.pj += total * (split / 100);
    acc.pf += total * ((100 - split) / 100);

    return acc;
  }, { total: 0, pj: 0, pf: 0 });
};

export const getMonthlyData = (invoices) => {
  const months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
  const group = {};

  invoices.forEach(inv => {
    if (!inv.competencia) return;

    const [, mes] = inv.competencia.split('/');
    const monthIdx = Number(mes) - 1;
    if (isNaN(monthIdx) || monthIdx < 0 || monthIdx > 11) return;

    const monthName = months[monthIdx];
    const total = (inv.valorOriginal ?? 0) + (Number(inv.incremento) || 0); // ← fix aqui

    group[monthName] = (group[monthName] || 0) + total;
  });

  return months.map(m => ({ name: m, valor: group[m] || 0 })).filter(m => m.valor > 0);
};