package com.botov.sferaHelper.service;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketDto;

import java.io.IOException;
import java.util.Collections;

public class SferaHelperMethods {

    public static ListTicketsDto listTicketsByQuery(String query) throws IOException {
        var response = SferaService.INSTANCE.listTicketsByQuery(query, 1000).execute();
        System.out.println("response=" + response);
        System.out.println("response.body()=" + response.body());

        return response.body();
    }

    public static void setDueDate(TicketDto ticket, String dueDate) throws IOException {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setDueDate(dueDate);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void setEstimation(TicketDto ticket, long estimation) throws IOException {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setEstimation(estimation);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void setParent(TicketDto ticket, String parent) throws IOException {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setParent(parent);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    //TODO not working
    public static void setSystem(TicketDto ticket, String system) throws IOException {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setSystems(Collections.singleton(system));
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void patchTicket(String number, TicketDto ticketDto) throws IOException {
        System.out.println("patch " + number + " with " + ticketDto);
        SferaService.INSTANCE.patchTicket(number, ticketDto).execute();
    }

}
