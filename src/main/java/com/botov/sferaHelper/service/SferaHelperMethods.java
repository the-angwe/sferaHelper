package com.botov.sferaHelper.service;

import com.botov.sferaHelper.bo.TicketType;
import com.botov.sferaHelper.dto.*;
import retrofit2.Response;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SferaHelperMethods {

    public static ListTicketsDto listTicketsByQuery(String query) throws IOException {
        int page = 0;
        ListTicketsDto body = new ListTicketsDto();
        body.setContent(new ArrayList<>());
        Response<ListTicketsDto> response;
        do {
            response = SferaService.INSTANCE.listTicketsByQuery(query, 1000, page++).execute();
//            System.out.println("response=" + response);
//            System.out.println("body=" + body);
            body.getContent().addAll(response.body().getContent());
            body.setTotalElements(response.body().getTotalElements());
        } while (response.body().getTotalElements().intValue() > body.getContent().size());
        return body;
    }

    public static ListSprintDto listSprints(String areaCode, String keyword) throws IOException {
        int page = 0;
        ListSprintDto body = new ListSprintDto();
        body.setContent(new ArrayList<>());
        Response<ListSprintDto> response;
        do {
            response = SferaService.INSTANCE.listSprints(areaCode, keyword, 1000, page++).execute();
//            System.out.println("response=" + response);
//            System.out.println("body=" + body);
            body.getContent().addAll(response.body().getContent());
            body.setTotalElements(response.body().getTotalElements());
        } while (response.body().getTotalElements().intValue() > body.getContent().size());
        return body;
    }

    public static GetTicketDto ticketByNumber(String number) throws IOException {
        var response = SferaService.INSTANCE.getTicket(number).execute();
//        System.out.println("response=" + response);
//        System.out.println("response.body()=" + response.body());
        return response.body();
    }

    public static void setDueDate(String number, String dueDate) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setDueDate(dueDate);
        patchTicket(number, ticketDto);
    }

    public static void setEstimation(String number, long estimation) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setEstimation(estimation);
        patchTicket(number, ticketDto);
    }

    public static void setParent(String number, String parent) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setParent(parent);
        patchTicket(number, ticketDto);
    }

    public static void setSystem(String number, String system) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setSystems(Collections.singleton(system));
        patchTicket2(number, ticketDto);
    }

    public static void patchTicket(String number, PatchTicketDto ticketDto) throws IOException {
        System.out.println("patch " + number + " with " + ticketDto);
        SferaService.INSTANCE.patchTicket(number, ticketDto).execute();
    }

    public static void setTicketType(String number, TicketType ticketType) throws IOException {
        PatchTicketDto ticketDto = ticketType.getPatchTicketDto();
        patchTicket2(number, ticketDto);
    }

    private static void patchTicket2(String number, PatchTicketDto ticketDto) throws IOException {
        System.out.println("patch2 " + number + " with " + ticketDto);
        SferaService.INSTANCE.patchTicket2(number, ticketDto).execute();
    }

    public static void setProject(String number, String project) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setProjectConsumer(Collections.singleton(project));
        patchTicket2(number, ticketDto);
    }

    public static void setSprint(String number, String sprint) throws IOException {
        if (sprint == null) {
            AttributesDto attribute = new AttributesDto();
            attribute.setNumber(number);
            attribute.setAttribute("sprint");
            SferaService.INSTANCE.deleteAttributes(List.of(attribute)).execute();
        } else {
            PatchTicketDto ticketDto = new PatchTicketDto();
            PatchSprintDto patchSprintDto = new PatchSprintDto();
            patchSprintDto.setId(Integer.parseInt(sprint));
            ticketDto.setSprint(Collections.singleton(patchSprintDto));
            patchTicket2(number, ticketDto);
        }
    }

    public static void setStatus(String number, String status) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setStatus(status);
        patchTicket2(number, ticketDto);
    }

    public static TicketCopyResponseDto copyTicket(GetTicketDto ticket) throws IOException {
        System.out.println("copy " + ticket.getNumber());
        TicketCopyRequestDto request = new TicketCopyRequestDto();
        request.setEntity(ticket.getNumber());
        request.setOverride(new OverrideDto());
        request.getOverride().setName(ticket.getName());
        var response = SferaService.INSTANCE.copyTicket(request).execute();
        System.out.println("response=" + response);
        System.out.println("response.body()=" + response.body());
        return response.body();
    }

    public static void setResolution(String number, String resolution) throws IOException {
        PatchTicketDto ticketDto = new PatchTicketDto();
        ticketDto.setResolution(resolution);
        patchTicket2(number, ticketDto);
    }

    public static SprintDto getCurrentSprint(String area) throws IOException {
        OffsetDateTime now = OffsetDateTime.now();
        ListSprintDto sprints = listSprints(area, now.format(DateTimeFormatter.ofPattern("yyyy")));

        for (SprintDto sprint: sprints.getContent()) {
            OffsetDateTime begin, end;
            begin = OffsetDateTime.parse(sprint.getStartDate());
            end = OffsetDateTime.parse(sprint.getEndDate());
            if ("sprint".equals(sprint.getType()) && begin.isBefore(now) && end.isAfter(now)) {
//                System.err.println(sprint.getName());
                return sprint;
            }
        }
        throw new RuntimeException("Current sprint not found");
    }

    public static SprintDto getNextSprint(String area) throws IOException {
        OffsetDateTime now = OffsetDateTime.now().plusDays(2);
        ListSprintDto sprints = listSprints(area, now.format(DateTimeFormatter.ofPattern("yyyy")));

        for (SprintDto sprint: sprints.getContent()) {
            OffsetDateTime begin, end;
            begin = OffsetDateTime.parse(sprint.getStartDate());
            end = OffsetDateTime.parse(sprint.getEndDate());
            if ("sprint".equals(sprint.getType()) && begin.isBefore(now) && end.isAfter(now)) {
//                System.err.println(sprint.getName());
                return sprint;
            }
        }
        throw new RuntimeException("Next sprint not found");
    }
}
