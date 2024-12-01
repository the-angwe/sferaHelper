package com.botov.sferaHelper.bo;

import com.botov.sferaHelper.dto.GetTicketDto;

public enum TicketType {
    TECH_DEBT(true),
    ARH(true),
    NEW_FUNC(true),
    DEFECT(false),
    OTHER(true);

    private final boolean canChange;

    private TicketType(boolean canChange) {
        this.canChange = canChange;
    }

    public static TicketType getTicketType(GetTicketDto ticket) {
        //TODO
        return null;
    }

    public boolean isCanChange() {
        return canChange;
    }

}
