package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SferaHelperTechDebtIB {

    public static void main(String... args) throws IOException {
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and workGroup=\"Технический долг\"";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        List<GetTicketDto> techDebtIBs = new ArrayList<>();
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());
            if (ticket.isTechDebtIB()) {
                techDebtIBs.add(ticket);
            }
        }

        System.out.println();
        System.out.println("TechDebtIBs:");
        for (GetTicketDto ticket : techDebtIBs) {
            System.out.println(SferaMonitoring.SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
