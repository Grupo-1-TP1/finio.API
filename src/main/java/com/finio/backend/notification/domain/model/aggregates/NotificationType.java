package com.finio.backend.notification.domain.model.aggregates;

public enum NotificationType {
    BUDGET_ALERT,      // Alerta de presupuesto excedido o cercano al límite
    SAVING_REMINDER,   // Recordatorio para aportar a una meta de ahorro
    RECURRING_PAYMENT, // Alerta de que se acerca un gasto fijo (ej. mensualidad)
    SYSTEM             // Avisos generales del sistema
}
