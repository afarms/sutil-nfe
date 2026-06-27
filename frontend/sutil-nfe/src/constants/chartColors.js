// Cores para gráficos
export const CHART_COLORS = {
  primary: '#6366f1',    // Indigo
  secondary: '#94a3b8',  // Slate
  success: '#10b981',    // Emerald
  warning: '#f59e0b',    // Amber
  danger: '#f43f5e',     // Rose
  light: '#f1f5f9',      // Slate 100
  medium: '#e2e8f0',     // Slate 200
};

// Cores específicas para gráficos
export const PIE_CHART_COLORS = [
  CHART_COLORS.primary,
  CHART_COLORS.secondary,
  CHART_COLORS.success,
  CHART_COLORS.warning,
  CHART_COLORS.danger,
];

// Cores para status do MEI
export const MEI_STATUS_COLORS = {
  exceeded: {
    color: 'text-rose-500',
    bgColor: 'bg-rose-50',
    borderColor: 'border-rose-100',
    label: 'LIMITE ULTRAPASSADO',
    chart: '#f43f5e'
  },
  warning: {
    color: 'text-amber-500',
    bgColor: 'bg-amber-50',
    borderColor: 'border-amber-100',
    label: 'ANDAMENTO (ALERTA)',
    chart: '#f59e0b'
  },
  normal: {
    color: 'text-emerald-500',
    bgColor: 'bg-emerald-50',
    borderColor: 'border-emerald-100',
    label: 'OBJETIVO (VERDE)',
    chart: '#10b981'
  }
};