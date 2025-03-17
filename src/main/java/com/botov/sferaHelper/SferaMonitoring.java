package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SferaMonitoring {

    public static void main(String... args) throws IOException {
        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
        checkTicketsWithoutSprint();
        checkProdBugs();

        //задачи с неправильным проектом
        //Вообще любый задачи не по 1553 (особенно по 1672_3)

        //истории без критериев приёмки
        //ключевые поставки стрима??
        //Новая функциональность по нецелевым ИС?

        //новые этики на мне
        //эпики без оценок, без критериев приёмка, без декопозиции
        //просроченные РДСы и РДСы в статус "создано" или с открытыми вопросами
    }

    public static void checkProdBugs() throws IOException {
        //баги прода
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

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.out.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.out.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setEstimation(ticket.getNumber(), 3600L);
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutSprint() throws IOException {
        //задачи вне спринтов
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.out.println("задачи вне спринтов (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.out.println(ticket.getNumber());
        }
    }
}
