package com.botov.sferaHelper.service;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.PatchTicketDto;

import java.io.IOException;
import java.util.Collections;

public class SferaHelperMethods {

    public static ListTicketsDto listTicketsByQuery(String query) throws IOException {
        var response = SferaService.INSTANCE.listTicketsByQuery(query, 1000).execute();
        System.out.println("response=" + response);
        System.out.println("response.body()=" + response.body());
        return response.body();
    }

    public static GetTicketDto ticketByNumber(String number) throws IOException {
        var response = SferaService.INSTANCE.getTicket(number).execute();
        System.out.println("response=" + response);
        System.out.println("response.body()=" + response.body());
        return response.body();
    }

    public static void setDueDate(ListTicketShortDto ticket, String dueDate) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setDueDate(dueDate);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void setEstimation(ListTicketShortDto ticket, long estimation) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setEstimation(estimation);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void setParent(ListTicketShortDto ticket, String parent) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setParent(parent);
        patchTicket(ticket.getNumber(), ticketDto);
    }

    //TODO not working
    public static void setSystem(ListTicketShortDto ticket, String system) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setSystems(Collections.singleton(system));
        patchTicket(ticket.getNumber(), ticketDto);
    }

    public static void patchTicket(String number, PatchTicketDto ticketDto) throws IOException {
        System.out.println("patch " + number + " with " + ticketDto);
        SferaService.INSTANCE.patchTicket(number, ticketDto).execute();
    }

}
