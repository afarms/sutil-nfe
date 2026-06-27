// Configurações relacionadas ao limite MEI
export const MEI_LIMITS = {
  // Percentual para considerar alerta (85% do limite)
  WARNING_THRESHOLD: 0.85, // 85%
  
  // Mensagens de status
  STATUS_MESSAGES: {
    EXCEEDED: {
      msg: 'Excedente:',
      label: 'LIMITE ULTRAPASSADO'
    },
    WARNING: {
      msg: 'Restante:',
      label: 'ANDAMENTO (ALERTA)'
    },
    NORMAL: {
      msg: 'Faltam para atingir:',
      label: 'OBJETIVO (VERDE)'
    }
  }
};