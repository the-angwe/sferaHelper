package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.SprintDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SferaSprintOpener {

    public static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";
    public static final String AREA = "DVPS";

    public static void main(String... args) throws IOException {
        removeContainerTicketsFromSprint();
        fixTicketsDueWithoutSprint();
        fixTicketsWithWrongDueDate();

        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
    }

    private static void fixTicketsDueWithoutSprint() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String begin, end;
        begin = OffsetDateTime.parse(sprint.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end = OffsetDateTime.parse(sprint.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + AREA + "' and status not in ('closed') and " +
                "dueDate <= '" + end + "' and dueDate >= '" + begin + "' and sprint != '" + sprint.getId().toString() + "'";

        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("С due date внутри спринта, но без указания спринта (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
//            SferaHelperMethods.setDueDate(ticket.getNumber(), OffsetDateTime.parse(ticket.getDueDate()).plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
    }

    private static void checkOverdueFRNRSAs() throws IOException {
        checkOverdue("FRNRSA", null);
    }

    private static void checkOverdue(String area, String filter) throws IOException {
        //просроченные РДСы
        String dueDate = LocalDate.now().plusDays(60).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + area + "' and status not in ('closed', 'done', 'rejectedByThePerformer') and " +
                "((dueDate = null) or (dueDate < '"
                + dueDate +
                "'))";
        if (filter != null) {
            query = query + " and " + filter;
        }
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("просроченные " + area + " (кол-во " + listTicketsDto.getContent().size() + "):");
        String newDueDate = LocalDate.now().plusDays(60).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setDueDate(ticket.getNumber(), newDueDate);
        }
    }

    private static void fixTicketsWithWrongDueDate() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String begin, end;
        begin = OffsetDateTime.parse(sprint.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end = OffsetDateTime.parse(sprint.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + AREA + "' and status not in ('closed') and " +
                "(dueDate = null or dueDate > '" + end + "' or dueDate < '" + begin + "') and sprint = '" + sprint.getId().toString() + "'";

        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Неправильная due date (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
//            SferaHelperMethods.setDueDate(ticket.getNumber(), end);
        }
    }

    public static void removeContainerTicketsFromSprint() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String query = "area='" + AREA + "' and status not in ('closed') and estimation > " + (3600L * 8 * 5 * 2) // 2w
                + " and sprint = '" + sprint.getId().toString() + "'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("контейнеры (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
//            SferaHelperMethods.setSprint(ticket.getNumber(), null);
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area='" + AREA + "' and status not in ('closed') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area='" + AREA + "' and status not in ('closed') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
