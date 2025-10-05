package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.SprintDto;
import com.botov.sferaHelper.dto.TicketDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SferaSprintCloser {

    private static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";
    private static final String AREA = "DVPS";

    public static void main(String... args) throws IOException {
        closeCurrentSprintTickets();
        fixClosedTicketsWithoutResolution();
        fixTicketsDueWithoutSprint();

        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
    }

    private static void closeCurrentSprintTickets() throws IOException {
        SprintDto currentSprint = SferaHelperMethods.getCurrentSprint(AREA);

        String query = "area='" + AREA + "' and status not in ('closed') and sprint = '" + currentSprint.getId() + "'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Незакрытые задачи текущего спринта (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
//            SferaHelperMethods.setStatus(ticket.getNumber(), "closed");
        }
    }

    private static void fixClosedTicketsWithoutResolution() throws IOException {
        //Закрытые задачи без резолюции
        String query = "area='" + AREA + "' and status in ('closed') and resolution = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Закрытые задачи без резолюции (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
            SferaHelperMethods.setResolution(ticket.getNumber(), "Готово");
        }
    }

    private static void fixTicketsDueWithoutSprint() throws IOException {
        SprintDto sprint = SferaHelperMethods.getCurrentSprint(AREA);

        String begin, end;
        begin = OffsetDateTime.parse(sprint.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end = OffsetDateTime.parse(sprint.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + AREA + "' and status not in ('closed') and " +
                "dueDate <= '" + end + "' and dueDate >= '" + begin + "' and sprint != '" + sprint.getId() + "'";

        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("С due date внутри спринта, но без указания спринта (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
//            SferaHelperMethods.setDueDate(ticket.getNumber(), OffsetDateTime.parse(ticket.getDueDate()).plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area='" + AREA + "' and status not in ('closed') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area='" + AREA + "' and status not in ('closed') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
