package com.botov.sferaHelper;

import com.botov.sferaHelper.bo.TicketType;
import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.*;

public class SferaHelperTicketTypesFixer {

    private static final HashMap<TicketType, Long> italonTicketTypesMap = new HashMap<>();
    static {
        italonTicketTypesMap.put(TicketType.NEW_FUNC, 40l);
        italonTicketTypesMap.put(TicketType.TECH_DEBT, 30l);
        italonTicketTypesMap.put(TicketType.ARH, 20l);
        italonTicketTypesMap.put(TicketType.DEFECT, 10l);
    }
    public static final int MAX_ERROR_IN_PERCENT = 1;

    public static final long MAX_ESTIMATION = 5*8*60*60;// 1 week

    public static final long MIN_ESTIMATION_STEP = 60*60;// 1 hour

    public static void main(String... args) throws IOException {
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = '4263'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        HashMap<TicketType, Set<GetTicketDto>> fullTicketsMap = new HashMap<>();
        HashMap<TicketType, Long> currentTicketTypesMap = new HashMap<>();
        for (TicketType ticketType : TicketType.values()) {
            currentTicketTypesMap.put(ticketType, 0l);
        }

        long fullEstimation = 0l;
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());

            long estimation = ensureEstimation(ticket);

            fullEstimation += estimation;
            TicketType ticketType = TicketType.getTicketType(ticket);
            currentTicketTypesMap.put(ticketType, currentTicketTypesMap.get(ticketType) + estimation);

            Set<GetTicketDto> fullTicketsMapSet = fullTicketsMap.get(ticketType);
            if (fullTicketsMapSet == null) {
                fullTicketsMapSet = new TreeSet<>(Comparator.comparing(GetTicketDto::getEstimation));
                fullTicketsMap.put(ticketType, fullTicketsMapSet);
            }
            fullTicketsMapSet.add(ticket);
        }

        for (TicketType ticketType : TicketType.values()) {
            italonTicketTypesMap.put(ticketType,
                    Integer.valueOf(Math.round((italonTicketTypesMap.getOrDefault(ticketType, 0L)*fullEstimation)/100L)).longValue()
            );
        }

        for (TicketType ticketType : TicketType.values()) {
            if (!ticketType.isCanChange()) {
                Long italon = italonTicketTypesMap.get(ticketType);
                Long curr = currentTicketTypesMap.get(ticketType);
                long diff = italon - curr;
                if (!match(diff, fullEstimation)) {
                    double estimationRate = ((double) italon) / curr;
                    for (GetTicketDto ticket : fullTicketsMap.get(ticketType)) {
                        long estimation = multiplyEstimation(ticket.getEstimation(), estimationRate);
                        ticket.setEstimation(estimation);
                        SferaHelperMethods.setEstimation(ticket.getNumber(), estimation);
                    }
                }
            }
        }


        System.out.println("currentTicketTypesMap = " + currentTicketTypesMap);
    }

    private static long ensureEstimation(GetTicketDto ticket) throws IOException {
        if (ticket.getEstimation() == null) {
            ticket.setEstimation(MIN_ESTIMATION_STEP);
            SferaHelperMethods.setEstimation(ticket.getNumber(), MIN_ESTIMATION_STEP);
        }
        return ticket.getEstimation();
    }

    //TODO check it
    private static long multiplyEstimation(Long estimation, double estimationRate) {
        long multiplyResult = Math.round((estimation == null ? 1 : estimation)  * estimationRate);
        long result = 0;
        while (result<multiplyResult) {
            result += MIN_ESTIMATION_STEP;
        }
        return Math.min(result, MAX_ESTIMATION);
    }

    private static boolean match(Long diff, Long fullEstimation) {
        return ((double) Math.abs(diff) / fullEstimation) * 100 < MAX_ERROR_IN_PERCENT;
    }


}
