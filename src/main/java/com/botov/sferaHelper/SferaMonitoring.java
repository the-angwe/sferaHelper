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

    public static void main(String... args) throws IOException {
        //checkProdBugs();
        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
        checkTicketsWithoutSprint();
        checkTicketsWithWrongSystems();
        checkTicketsWithWrongProject();
        checkCreatedRDSs();
        checkOverdueRDSs();
        checkRDSWithOpenQuestions();


        //истории без критериев приёмки
        //ключевые поставки стрима??
        //Новая функциональность по нецелевым ИС?

        //новые этики на мне
        //эпики без оценок, без критериев приёмка, без декопозиции

    }

    private static void checkRDSWithOpenQuestions() throws IOException {
        //RDS с открытыми вопросами
        String query = "area='RDS' and openQuestion = 'открытый вопрос'  and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS с открытыми вопросами (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(ticket.getNumber());
        }
    }

    private static void checkOverdueRDSs() throws IOException {
        //просроченные РДСы
        String dueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "area='RDS' and status not in ('closed', 'done', 'rejectedByThePerformer') and " +
                "((dueDate = null) or (dueDate < '"
                + dueDate +
                "')) and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("просроченные РДСы (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(ticket.getNumber());
        }
    }

    private static void checkCreatedRDSs() throws IOException {
        //RDS в статусe "Создано"
        String query = "area='RDS' and status in ('created') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS в статусe \"Создано\" (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(ticket.getNumber());
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
            System.err.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithWrongSystems() throws IOException {
        //Задачи не по 1553 (особенно по 1672_3)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems != \"1553 Заявки ФЛ\"";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Задачи не по 1553 (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setSystem(ticket.getNumber(), "\"1553 Заявки ФЛ\"");
            System.err.println(ticket.getNumber());
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

        System.err.println();
        System.err.println("дефекты прода (кол-во " + listTicketsDto.getContent().size() + "):");
        for (GetTicketDto ticket : prodDefects) {
            System.err.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEpics() throws IOException {
        //задачи без эпиков
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(ticket.getNumber());
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
            System.err.println(ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutSprint() throws IOException {
        //задачи вне спринтов
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи вне спринтов (кол-во " + listTicketsDto.getContent().size() + "):");
        for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
            System.err.println(ticket.getNumber());
        }
    }
}
