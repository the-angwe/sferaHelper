package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.*;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//мониторит корректность задач, по возможности простые проблемы исправляет сам
public class SferaMonitoring {

    public static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";

    public static void main(String... args) throws IOException {
//        checkProdBugs();
        checkClosedTicketsWithoutResolution();
        checkTicketsWithoutEpics();
        checkTicketsWithoutEstimation();
//        checkTicketsWithoutSprint();
//        checkEpicsWithWrongSystems();
//        checkTicketsWithWrongSystems();
//        checkTicketsWithWrongProject();
//        checkTicketsWithBigEstimation();
//        checkOverdueRDSs();
//        checkNotOnBotovRDSs();
//        checkOnBotovNotMySystemRDSs();
//        checkYellowDeadlineRDSs();
//        checkRedDeadlineRDSs();
//        checkRDSsStatus();
//        checkOverdueFRNRSAs();
//        checkRDSWithOpenQuestions();
//        checkStoriesWithoutAcceptanceCriteria();
//        checkEpicsWithoutEstimation();
//        checkEpicsWithoutAcceptanceCriteria();
//        checkEpicsWithoutOpenedChildren();
        //новые эпики на мне??

        //найти эпики без фичей
        //найти фичи без эпиков
        //найти фичи без подзадач
        //найти фичи без сторей
        //найти эпики, на которых хоть что то , кроме фичей
        //найти фичи, в которых трудооценка не на 0,87 больше, чем в эпиках
        //найти эпики без суперспринта и срока
        //найти фичи без суперспринта и срока

        //выполненные задачи, но не закрытые
//        testListTickets();
//        testListSprints();
    }

    private static void testListSprints() throws IOException {
        ListSprintDto listSprintDto = SferaHelperMethods.listSprints("DVPS", "2025.4");

        System.err.println();
        System.err.println("Test (кол-во " + listSprintDto.getContent().size() + "):");
        OffsetDateTime now = OffsetDateTime.now();
        for (SprintDto sprint: listSprintDto.getContent()) {
            OffsetDateTime begin, end;
            begin = OffsetDateTime.parse(sprint.getStartDate());
            end = OffsetDateTime.parse(sprint.getEndDate());
            if ("sprint".equals(sprint.getType()) && begin.isBefore(now) && end.isAfter(now)) {
                System.err.println(sprint.getName());
            }
        }
    }

    private static void testListTickets() throws IOException {
        //Закрытые задачи без резолюции
        String query = "area=\"DVPS\" and status not in ('closed')";
//        String query = "area=\"DVPS\" and status in ('closed') and resolution = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Test (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
//            SferaHelperMethods.setResolution(ticket.getNumber(), "Готово");
        }
    }

    private static void checkClosedTicketsWithoutResolution() throws IOException {
        //Закрытые задачи без резолюции
        String query = "area=\"DVPS\" and status in ('closed') and resolution = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Закрытые задачи без резолюции (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
            SferaHelperMethods.setResolution(ticket.getNumber(), "Готово");
        }
    }

    private static void checkOnBotovNotMySystemRDSs() throws IOException {
        //РДСы не по 1553_1, 1553 или 1672_3 на Ботове
        String query = "area=\"RDS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") ";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);


        List<TicketDto> notMySystemRDSs = new ArrayList<>();
        for (TicketDto ticket: listTicketsDto.getContent()) {
            if (!ticket.getName().startsWith("[1672_3]") &&
                    !ticket.getName().startsWith("[1553]") &&
                    !ticket.getName().startsWith("[1553_1]") &&
                    !ticket.getNumber().equals("RDS-272592")) {//консультация по постпроцессорной очереди
                notMySystemRDSs.add(ticket);
            }
        }

        System.err.println();
        System.err.println("РДСы не по 1553_1, 1553 или 1672_3 на Ботове (кол-во " + notMySystemRDSs.size() + "):");
        for (TicketDto ticket: notMySystemRDSs) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
        }
    }

    private static void checkNotOnBotovRDSs() throws IOException {
        //РДСы по 1553_1, 1553 или 1672_3 не на Ботове
        String query = "area=\"RDS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee not in (\"vtb70166052@corp.dev.vtb\") " +
                "and (name ~ \"[1672_3]\" or name ~ \"[1553]\" or name ~ \"[1553_1]\") " +
                "and (streamExecutor = 'Омниканальный мидл' or streamExecutor = 'Базовые сервисы' or streamExecutor='Омниканальные микросервисные решения' or streamConsumer = 'Омниканальный мидл' or streamConsumer = 'Базовые сервисы' or streamConsumer='Омниканальные микросервисные решения')";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        List<TicketDto> notMyRDSs = new ArrayList<>();
        for (TicketDto ticket: listTicketsDto.getContent()) {
            if (!ticket.getNumber().equals("RDS-313296")) {//консультация по постпроцессорной очереди
                notMyRDSs.add(ticket);
            }
        }
        System.err.println();
        System.err.println("РДСы по 1553_1, 1553 или 1672_3 не на Ботове (кол-во " + notMyRDSs.size() + "):");
        for (TicketDto ticket: notMyRDSs) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber() + " \"" + ticket.getName() + "\"");
        }
    }

    private static void checkYellowDeadlineRDSs() throws IOException {
        //"Пожелтевшие RDS" https://sfera.inno.local/knowledge/pages?id=1675408
        String query = "area=\"RDS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and label in ('YELLOW_DEADLINE_ALERT') " +
                " and (streamExecutor = 'Омниканальный мидл' or streamExecutor = 'Базовые сервисы' or streamExecutor='Омниканальные микросервисные решения' or streamConsumer = 'Омниканальный мидл' or streamConsumer = 'Базовые сервисы' or streamConsumer='Омниканальные микросервисные решения') ";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("\"Пожелтевшие RDS\" (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkRedDeadlineRDSs() throws IOException {
        //"Покрасневшие RDS" https://sfera.inno.local/knowledge/pages?id=1675665
        String query = "area=\"RDS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and label in ('RED_DEADLINE_MISSED') " +
                " and (streamExecutor = 'Омниканальный мидл' or streamExecutor = 'Базовые сервисы' or streamExecutor='Омниканальные микросервисные решения' or streamConsumer = 'Омниканальный мидл' or streamConsumer = 'Базовые сервисы' or streamConsumer='Омниканальные микросервисные решения') ";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("\"Покрасневшие RDS\" (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkEpicsWithoutOpenedChildren() throws IOException {
        //эпики без декопозиции
        String query = "area=\"STROMS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\") " +
                "and not hasOpenedChildren()";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("эпики без декопозиции (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
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
        for (TicketDto ticket: listTicketsDto.getContent()) {
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
        for (TicketDto ticket: listTicketsDto.getContent()) {
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
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkRDSWithOpenQuestions() throws IOException {
        //RDS с открытыми вопросами
        String query = "area='RDS' and openQuestion = 'открытый вопрос'  and status not in ('closed', 'done', 'rejectedByThePerformer') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS с открытыми вопросами (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkRDSsStatus() throws IOException {
        //RDS не в статусe "В очереди"
        String query = "area='RDS' and status not in ('closed', 'done', 'rejectedByThePerformer', 'onTheQueue') and assignee in (\"vtb70166052@corp.dev.vtb\", \"vtb4065673@corp.dev.vtb\", \"vtb70190852@corp.dev.vtb\", \"vtb4075541@corp.dev.vtb\", \"vtb4078565@corp.dev.vtb\", \"VTB4075541@corp.dev.vtb\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("RDS не в статусe \"В очереди\" (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
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
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setDueDate(ticket.getNumber(), newDueDate);
        }
    }

    public static void checkTicketsWithBigEstimation() throws IOException {
        //задачи с трудооценкой, большей чем 4 ч.д.
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation>" + (3600L * 8 * 4) ;
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи с трудооценкой, большей чем 4 ч.д. (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
            SferaHelperMethods.setEstimation(ticket.getNumber(), 3600L * 8 * 4);
        }
    }

    private static void checkTicketsWithWrongProject() throws IOException {
        //задачи с неправильным проектом (не 2973)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and projectConsumer != 'f9696ccf-0f8d-431e-a803-9d00ee6e3329'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи с неправильным проектом (не 2973) (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            //SferaHelperMethods.setProject(ticket.getNumber(), "f9696ccf-0f8d-431e-a803-9d00ee6e3329");// проект 2973
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkEpicsWithWrongSystems() throws IOException {
        //"Эпики" не по 1553 (особенно по 1672_3)
        String query = "area=\"STROMS\" and status not in ('closed', 'done', 'rejectedByThePerformer')  and assignee in (\"vtb70166052@corp.dev.vtb\") and systems not in (\"1553 Заявки ФЛ\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Эпики не по 1553 (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setSystem(ticket.getNumber(), "1553 Заявки ФЛ");
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithWrongSystems() throws IOException {
        //Задачи не по 1553 (особенно по 1672_3)
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems not in (\"1553 Заявки ФЛ\")";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("Задачи не по 1553 (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setSystem(ticket.getNumber(), "1553 Заявки ФЛ");
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
        for (TicketDto ticketDto : listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(ticketDto.getNumber());
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
        String query = "area=\"DVPS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без эпиков (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutEstimation() throws IOException {
        //задачи без оценок
        String query = "area=\"DVPS\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи без оценок (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            SferaHelperMethods.setEstimation(ticket.getNumber(), 3600 * 8);
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }

    private static void checkTicketsWithoutSprint() throws IOException {
        //задачи вне спринтов
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = null";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        System.err.println();
        System.err.println("задачи вне спринтов (кол-во " + listTicketsDto.getContent().size() + "):");
        for (TicketDto ticket: listTicketsDto.getContent()) {
            System.err.println(SFERA_TICKET_START_PATH + ticket.getNumber());
        }
    }
}
