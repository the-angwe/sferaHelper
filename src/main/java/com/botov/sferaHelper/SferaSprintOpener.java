package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.SprintDto;
import com.botov.sferaHelper.dto.TicketDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SferaSprintOpener {

    private static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";
    private static final String AREA = "DVPS";
    private static final String STROMS = "STROMS";

    public static void main(String... args) throws IOException {
        removeContainerTicketsFromSprint();
        fixTicketsDueWithoutSprint();
        fixTicketsWithWrongDueDate();

        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
        checkTicketsWithWrongParent();
    }

    private static void fixTicketsDueWithoutSprint() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String begin, end;
        begin = OffsetDateTime.parse(sprint.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end = OffsetDateTime.parse(sprint.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + AREA + "' and status not in ('closed') and " +
                "dueDate <= '" + end + "' and dueDate >= '" + begin + "' and sprint != '" + sprint.getId() + "'";

        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("С due date внутри спринта, но без указания спринта (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
            SferaHelperMethods.setDueDate(ticket.getNumber(), LocalDate.parse(ticket.getDueDate()).plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
    }

    private static void fixTicketsWithWrongDueDate() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String begin, end;
        begin = OffsetDateTime.parse(sprint.getStartDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end = OffsetDateTime.parse(sprint.getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='" + AREA + "' and status not in ('closed') and " +
                "(dueDate = null or dueDate > '" + end + "' or dueDate < '" + begin + "') and sprint = '" + sprint.getId() + "'";

        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Неправильная due date (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setDueDate(ticket.getNumber(), end);
        }
    }

    private static void removeContainerTicketsFromSprint() throws IOException {
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String query = "area='" + AREA + "' and status not in ('closed') and estimation > " + (3600L * 8 * 5 * 2) // 2w
                + " and sprint = '" + sprint.getId() + "'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("контейнеры (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setSprint(ticket.getNumber(), null);
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area='" + AREA + "' and status not in ('closed') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithWrongParent() throws IOException {
        SprintDto currentSupersprint = SferaHelperMethods.getCurrentSupersprint(AREA);
        SprintDto sprint = SferaHelperMethods.getNextSprint(AREA);

        String query = "area='" + STROMS +
                "' and status not in ('closed') and assignee in ('vtb70165782@corp.dev.vtb', 'vtb4067230@corp.dev.vtb')" +
                " and name ~ 'devsecops' and sprint = '" + currentSupersprint.getId() + "' and type = 'epic'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
        assert listTicketsDto.getContent().size() == 1;
        TicketDto parent = listTicketsDto.getContent().get(0);

        query = "area='" + AREA + "' and status not in ('closed') and parent != '" + parent.getNumber() +
                "' and sprint = '" + sprint.getId() + "'";
        listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи с неправильной родительской (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area='" + AREA + "' and status not in ('closed') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket : listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
