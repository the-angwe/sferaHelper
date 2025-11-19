package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.SprintDto;
import com.botov.sferaHelper.dto.TicketCopyResponseDto;
import com.botov.sferaHelper.dto.TicketDto;
import com.botov.sferaHelper.service.SferaHelperMethods;
import com.botov.sferaHelper.service.SferaServiceImpl;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SferaYearPlanner {

    private static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";
    private static final String AREA = "DVPS";
    private static final String username = "vtb70165782@corp.dev.vtb";

    public static void main(String... args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        String token = SferaHelperMethods.sferaLogin(username, Files.readAllLines(Paths.get("p.txt")).get(0)).getAccess_token();
        SferaServiceImpl.INSTANCE = SferaServiceImpl.createSferaService(token);

        createEpics();
    }

    private static void createEpics() throws IOException {
        SprintDto currentSupersprint = SferaHelperMethods.getCurrentSupersprint(AREA);
        TicketDto parent = SferaHelperMethods.getEpicOfSupersprint(currentSupersprint);
        List<SprintDto> supersprints = SferaHelperMethods.getSupersprintsOfNextYear(AREA);

        for (int i = 0; i < 4; i++) {
            String end = OffsetDateTime.parse(supersprints.get(i).getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            TicketCopyResponseDto ticketCopy = SferaHelperMethods.copyTicket2(parent, String.format("[%s.%d] Обеспечение процессов DevSecOps", end.substring(0, 4), i + 1));

            SferaHelperMethods.setSprint(ticketCopy.getNumber(), supersprints.get(i).getId().toString());
            SferaHelperMethods.setDueDate(ticketCopy.getNumber(), end);
        }
    }
}
