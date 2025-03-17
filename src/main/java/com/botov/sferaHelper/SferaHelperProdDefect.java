package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SferaHelperProdDefect {

    public static void main(String... args) throws IOException {
        String query = "type=\"defect\" and area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer')";
        //String query = "area=\"FRNRSA\" and number='FRNRSA-7274'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        List<GetTicketDto> prodDefects = new ArrayList<>();
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());
            if (ticket.isProdDefect()) {
                prodDefects.add(ticket);
            }
        }

        System.out.println();
        System.out.println("ProdBugs:");
        for (GetTicketDto ticket : prodDefects) {
            System.out.println(ticket.getNumber());
        }
    }
}
