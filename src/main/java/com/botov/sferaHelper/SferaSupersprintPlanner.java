package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.SprintDto;
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

public class SferaSupersprintPlanner {

    private static final String SFERA_TICKET_START_PATH = "https://sfera.inno.local/tasks/task/";
    private static final String AREA = "DVPS";
    private static final String STROMS = "STROMS";
    private static final String username = "vtb70165782@corp.dev.vtb";

    public static void main(String... args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        String token = SferaHelperMethods.sferaLogin(username, Files.readAllLines(Paths.get("p.txt")).get(0)).getAccess_token();
        SferaServiceImpl.INSTANCE = SferaServiceImpl.createSferaService(token);

        addContainerTicketsToSprints();
    }

    private static void addContainerTicketsToSprints() throws IOException {
        SprintDto next = SferaHelperMethods.getNextSupersprint(AREA);
        List<SprintDto> sprints = SferaHelperMethods.getSprintsOfSupersprint(AREA, next);
        String[][] tasks = {
                {"DVPS-92", "DVPS-333"},
                {"DVPS-93", "DVPS-438"},
                {"DVPS-94"},
                {"DVPS-95"},
                {"DVPS-436"},
                {"DVPS-437"}
        };
        TicketDto parent = SferaHelperMethods.getEpicOfSupersprint(next);

        for (int i = 0; i < 6; i++) {
            String end = OffsetDateTime.parse(sprints.get(i).getEndDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (String task : tasks[i]) {
//                SferaHelperMethods.setSprint(task, sprints.get(i).getId().toString());
//                SferaHelperMethods.setDueDate(task, end);
//                SferaHelperMethods.setParent(task, parent.getId().toString());
            }
        }
    }
}
