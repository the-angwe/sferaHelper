package com.botov.sferaHelper.bo;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.PatchTicketDto;

import java.util.function.Supplier;

public enum TicketType {
    TECH_DEBT(true, () -> {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setWorkGroup("Технический долг");
        ticketDto.setTechDebtConsequence("Другое");
        return ticketDto;
    }),
    ARH(true, () -> {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setWorkGroup("Архитектурная задача");
        ticketDto.setArchTaskReason("Прочие архитектурные задачи");
        return ticketDto;
    }),
    NEW_FUNC(true, () -> {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setWorkGroup("Новая функциональность");
        return ticketDto;
    }),
    DEFECT(false, () -> {
        throw new RuntimeException("DEFECT is not for patch");
    }),
    OTHER(false, () -> {
        throw new RuntimeException("OTHER is not for patch");
    });

    private final boolean canChange;//TODO тех.долг ИБ не может меняться
    private final Supplier<PatchTicketDto> patchTicketDtoSupplier;

    TicketType(boolean canChange, Supplier<PatchTicketDto> patchTicketDtoSupplier) {
        this.canChange = canChange;
        this.patchTicketDtoSupplier = patchTicketDtoSupplier;
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

    public boolean isCanChange(GetTicketDto ticket) {
        if (!isCanChange()) {
            return false;
        }

        if (ticket.isTechDebtIB()) {
            return false;
        }
        return true;
    }

    public PatchTicketDto getPatchTicketDto() {
        return patchTicketDtoSupplier.get();
    }


}
