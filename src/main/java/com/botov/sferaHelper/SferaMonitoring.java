package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SferaMonitoring {

    public static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";

    public static void main(String... args) throws IOException {
        checkProdBugs();
        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
        checkTicketsWithoutSprint();
        checkTicketsWithWrongSystems();
        checkTicketsWithWrongProject();
        checkOverdueRDSs();
        checkRDSsStatus();
        checkOverdueFRNRSAs();
        checkRDSWithOpenQuestions();
        checkStoriesWithoutAcceptanceCriteria();
        checkEpicsWithoutEstimation();
        checkEpicsWithoutAcceptanceCriteria();
        checkEpicsWithoutOpenedChildren();

        //новые эпики на мне??
    }

    private static void checkEpicsWithoutOpenedChildren() throws IOException {
        //эпики без декопозиции
        String query = "area=\"STROMS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and not hasOpenedChildren()";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("эпики без декопозиции (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkEpicsWithoutAcceptanceCriteria() throws IOException {
        //эпики без критериев приёмки
        String query = "area=\"STROMS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and (acceptanceCriteria=null or acceptanceCriteria='' or acceptanceCriteria='!' or acceptanceCriteria='-' or acceptanceCriteria=' ')";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("эпики без критериев приёмки (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkEpicsWithoutEstimation() throws IOException {
        //эпики без оценок
        String query = "area=\"STROMS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("эпики без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkStoriesWithoutAcceptanceCriteria() throws IOException {
        //истории без критериев приёмки
        String query = "type=\"story\" and area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') " +
                "and (acceptanceCriteria=null or acceptanceCriteria='' or acceptanceCriteria='-' or acceptanceCriteria=' ')";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("истории без критериев приёмки (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkRDSWithOpenQuestions() throws IOException {
        //RDS с открытыми вопросами
        String query = "area='RDS' and openQuestion = 'открытый вопрос'  and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS с открытыми вопросами (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkRDSsStatus() throws IOException {
        //RDS не в статусe "В очереди"
        String query = "area='RDS' and status not in ('closed', 'done', 'rejectedByThePerformer', 'onTheQueue') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS не в статусe \"В очереди\" (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setStatus(ticket.getNumber(), "onTheQueue");
        }
    }


    private static void checkOverdueRDSs() throws IOException {
        checkOverdue("RDS", "assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")");
    }

    private static void checkOverdueFRNRSAs() throws IOException {
        checkOverdue("FRNRSA", null);
    }

    private static void checkOverdue(String area, String filter) throws IOException {
        //просроченные РДСы
        String dueDate = LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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

    private static void checkTicketsWithWrongProject() throws IOException {
        //задачи с неправильным проектом (не 2973)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and projectConsumer != 'f9696ccf-0f8d-431e-a803-9d00ee6e3329'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи с неправильным проектом (не 2973) (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setProject(ticket.getNumber(), "f9696ccf-0f8d-431e-a803-9d00ee6e3329");// проект 2973
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithWrongSystems() throws IOException {
        //Задачи не по 1553 (особенно по 1672_3)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems not in (\"1553 Заявки ФЛ\", \"1553_1 Распоряжения ФЛ\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Задачи не по 1553 (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setSystem(ticket.getNumber(), "\"1553 Заявки ФЛ\"");
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    public static void checkProdBugs() throws IOException {
        //дефекты прода
        String query = "type=\"defect\" and area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer')";
        //String query = "type=\"defect\" and area=\"FRNRSA\" and createDate >= '2025-01-01' and systems != \"1672_3 Аутентификация подтверждение операций\"";
        //String query = "area=\"FRNRSA\" and number='FRNRSA-7274'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        List<GetTicketDto> prodDefects = new ArrayList<>();
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());
            if (ticket.isProdDefect()) {
                prodDefects.add(ticket);
            }
        }

        System.err.println();
        System.err.println("дефекты прода (кол-во " + prodDefects.size() + "):");
        for (GetTicketDto ticket : prodDefects) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setEstimation(ticket.getNumber(), 3600L);
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutSprint() throws IOException {
        //задачи вне спринтов
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи вне спринтов (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
