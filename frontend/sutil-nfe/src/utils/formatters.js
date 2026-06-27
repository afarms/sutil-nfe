export const formatCurrency = (value) => {
  return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
};

export const formatPercentage = (value) => {
  return `${value.toFixed(0)}%`;
};