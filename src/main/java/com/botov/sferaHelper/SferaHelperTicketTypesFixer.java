package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.PatchTicketDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.HashMap;

public class SferaHelperTicketTypesFixer {

    private static final HashMap<String, Integer> italonTicketTypesMap = new HashMap<>();

    {
        italonTicketTypesMap.put("Новая функциональность", 40);
        italonTicketTypesMap.put("Технический долг", 30);
        italonTicketTypesMap.put("Архитектура", 20);
        italonTicketTypesMap.put("Дефект", 10);
    }

    public static void main(String... args) throws IOException {
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = '4263'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        HashMap<String, Integer> currentTicketTypesMap = new HashMap<>();
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());
            System.out.println();
            //SferaHelperMethods.setSystem(ticket, "\"1672_3 Аутентификация подтверждение операций\"");
            //SferaHelperMethods.setParent(ticket, "STROMS-3199");
            //SferaHelperMethods.setEstimation(ticket, 3600L);
            //SferaHelperMethods.setDueDate(ticket, "2025-03-31");
        }
        System.out.println("currentTicketTypesMap = " + currentTicketTypesMap);
    }

}
