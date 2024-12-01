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

        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());

            ensureEstimation(ticket);
            TicketType ticketType = TicketType.getTicketType(ticket);

            Set<GetTicketDto> fullTicketsMapSet = fullTicketsMap.get(ticketType);
            if (fullTicketsMapSet == null) {
                fullTicketsMapSet = new TreeSet<>(Comparator.comparing(GetTicketDto::getEstimation));
                fullTicketsMap.put(ticketType, fullTicketsMapSet);
            }
            fullTicketsMapSet.add(ticket);
        }

        long fullEstimation = calcFullEstimation(fullTicketsMap);

        for (TicketType ticketType : TicketType.values()) {
            if (!ticketType.isCanChange()) {
                Long italon = calcItalonEstimation(ticketType, fullEstimation);
                Long curr = calcEstimation(ticketType, fullTicketsMap);
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

        System.out.println("fullTicketsMap = " + fullTicketsMap);
    }

    private static Long calcItalonEstimation(TicketType ticketType, long fullEstimation) {
        return Integer.valueOf(Math.round((italonTicketTypesMap.getOrDefault(ticketType, 0L)*fullEstimation)/100L)).longValue();
    }

    private static long calcFullEstimation(HashMap<TicketType, Set<GetTicketDto>> fullTicketsMap) {
        long result = 0l;
        for (TicketType ticketType : fullTicketsMap.keySet()) {
            result += calcEstimation(ticketType, fullTicketsMap);
        }
        return result;
    }

    private static long calcEstimation(TicketType ticketType, HashMap<TicketType, Set<GetTicketDto>> fullTicketsMap) {
        long result = 0l;
        Set<GetTicketDto> tickets = fullTicketsMap.get(ticketType);
        for (GetTicketDto ticket : tickets) {
            result += ticket.getEstimation();
        }
        return result;
    }

    private static long ensureEstimation(GetTicketDto ticket) throws IOException {
        if (ticket.getEstimation() == null) {
            ticket.setEstimation(MIN_ESTIMATION_STEP);
            SferaHelperMethods.setEstimation(ticket.getNumber(), MIN_ESTIMATION_STEP);
        }
        return ticket.getEstimation();
    }

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
