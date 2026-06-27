import { useMemo } from 'react';
import { MEI_STATUS_COLORS } from '../constants/chartColors';
import { MEI_LIMITS } from '../constants/meiLimits';

export const useMeiStatus = (total, metaAnual) => {
  return useMemo(() => {
    const faltante = metaAnual - total;
    const percentual = total / metaAnual;

    if (total > metaAnual) {
      return {
        ...MEI_STATUS_COLORS.exceeded,
        value: total - metaAnual,
        msg: MEI_LIMITS.STATUS_MESSAGES.EXCEEDED.msg,
        label: MEI_LIMITS.STATUS_MESSAGES.EXCEEDED.label
      };
    } else if (percentual >= MEI_LIMITS.WARNING_THRESHOLD) {
      return {
        ...MEI_STATUS_COLORS.warning,
        value: faltante,
        msg: MEI_LIMITS.STATUS_MESSAGES.WARNING.msg,
        label: MEI_LIMITS.STATUS_MESSAGES.WARNING.label
      };
    } else {
      return {
        ...MEI_STATUS_COLORS.normal,
        value: faltante,
        msg: MEI_LIMITS.STATUS_MESSAGES.NORMAL.msg,
        label: MEI_LIMITS.STATUS_MESSAGES.NORMAL.label
      };
    }
  }, [total, metaAnual]);
};