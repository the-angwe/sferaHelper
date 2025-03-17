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
        checkProdBugs();
        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
        checkTicketsWithoutSprint();
        checkTicketsWithWrongSystems();
        checkTicketsWithWrongProject();


        //истории без критериев приёмки
        //ключевые поставки стрима??
        //Новая функциональность по нецелевым ИС?

        //новые этики на мне
        //эпики без оценок, без критериев приёмка, без декопозиции
        //просроченные РДСы и РДСы в статус "создано" или с открытыми вопросами
    }

    private static void checkTicketsWithWrongProject() throws IOException {
        //задачи с неправильным проектом (не 2973)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and projectConsumer != 'f9696ccf-0f8d-431e-a803-9d00ee6e3329'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.err.println("задачи с неправильным проектом (не 2973) (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setProject(ticket.getNumber(), "f9696ccf-0f8d-431e-a803-9d00ee6e3329");// проект 2973
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithWrongSystems() throws IOException {
        //Задачи не по 1553 (особенно по 1672_3)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems != \"1553 Заявки ФЛ\"";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.err.println("Задачи не по 1553 (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setSystem(ticket.getNumber(), "\"1553 Заявки ФЛ\"");
            System.out.println(ticket.getNumber());
        }
    }

    public static void checkProdBugs() throws IOException {
        //дефекты прода
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
        System.err.println("дефекты прода (кол-во " + listTicketsDto.getContent().size() + "):");
        for (GetTicketDto ticket : prodDefects) {
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setEstimation(ticket.getNumber(), 3600L);
            System.out.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutSprint() throws IOException {
        //задачи вне спринтов
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        System.err.println("задачи вне спринтов (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.out.println(ticket.getNumber());
        }
    }
}
