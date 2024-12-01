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
    public static final int MAX_ERROR_IN_PERCENT = 5;

    //TODO
    public static final int MAX_ESTIMATION = 5*8*60*60;// 1 week

    //TODO
    public static final int MIN_ESTIMATION_STEP = 60*60;// 1 hour

    {
        italonTicketTypesMap.put(TicketType.NEW_FUNC, 40l);
        italonTicketTypesMap.put(TicketType.TECH_DEBT, 30l);
        italonTicketTypesMap.put(TicketType.ARH, 20l);
        italonTicketTypesMap.put(TicketType.DEFECT, 10l);
    }

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
            long estimation = ticket.getEstimation() == null ? 0 : ticket.getEstimation();
            fullEstimation += estimation;
            TicketType ticketType = TicketType.getTicketType(ticket);
            currentTicketTypesMap.put(ticketType, currentTicketTypesMap.get(ticketType) + estimation);

            Set<GetTicketDto> fullTicketsMapSet = fullTicketsMap.get(ticketType);
            if (fullTicketsMapSet == null) {
                fullTicketsMapSet = new HashSet<>();
                fullTicketsMap.put(ticketType, fullTicketsMapSet);
            }
            fullTicketsMapSet.add(ticket);
        }

        for (TicketType ticketType : TicketType.values()) {
            italonTicketTypesMap.put(ticketType, Integer.valueOf(Math.round((italonTicketTypesMap.get(ticketType)*fullEstimation)/100L)).longValue());
        }

        TreeMap<Long, TicketType> diffs = new TreeMap<>();
        TreeMap<TicketType, Long> diffs2 = new TreeMap<>();
        for (TicketType ticketType : TicketType.values()) {
            Long italon = italonTicketTypesMap.get(ticketType);
            Long curr = currentTicketTypesMap.get(ticketType);
            long diff = italon - curr;
            diffs.put(diff, ticketType);
            diffs2.put(ticketType, diff);
        }


        for (TicketType ticketType : TicketType.values()) {
            if (!ticketType.isCanChange()) {
                Long diff = diffs2.get(ticketType);
                if (!match(diff, fullEstimation)) {
                    double estimationRate = ((double) diff) / fullEstimation;
                    for (GetTicketDto ticket : fullTicketsMap.get(ticketType)) {
                        SferaHelperMethods.setEstimation(ticket.getNumber(), multiplyEstimation(ticket.getEstimation(), estimationRate));
                    }
                }
            } else {

            }
        }


        System.out.println("currentTicketTypesMap = " + currentTicketTypesMap);
    }

    //TODO check it
    private static long multiplyEstimation(Long estimation, double estimationRate) {
        //TODO why 1?
        long multiplyResult = Math.round((estimation == null ? 1 : estimation)  * estimationRate);
        long result = 0;
        while (result<multiplyResult) {
            result += MIN_ESTIMATION_STEP;
        }
        return Math.min(result, MAX_ESTIMATION);
    }

    private static boolean match(Long diff, Long fullEstimation) {
        return (diff / fullEstimation) * 100 < MAX_ERROR_IN_PERCENT;
    }


}
