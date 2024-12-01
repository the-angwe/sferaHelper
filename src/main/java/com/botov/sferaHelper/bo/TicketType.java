package com.botov.sferaHelper.bo;

import com.botov.sferaHelper.dto.GetTicketDto;

public enum TicketType {
    TECH_DEBT(true),
    ARH(true),
    NEW_FUNC(true),
    DEFECT(false),
    OTHER(true);

    private final boolean canChange;//TODO тех.долг ИБ не может меняться

    TicketType(boolean canChange) {
        this.canChange = canChange;
    }

    public static TicketType getTicketType(GetTicketDto ticket) {
        if (ticket.getType().getName().equals("Дефект")) {
            return DEFECT;
        }
        if (ticket.getWorkGroup().getName().equals("Новая функциональность")) {
            return NEW_FUNC;
        }
        if (ticket.getWorkGroup().getName().equals("Технический долг")) {
            return TECH_DEBT;
        }
        if (ticket.getWorkGroup().getName().equals("Архитектурная задача")) {
            return ARH;
        }
        return OTHER;
    }

    public boolean isCanChange() {
        return canChange;
    }

}
