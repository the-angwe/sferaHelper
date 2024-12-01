package com.botov.sferaHelper;

import com.botov.sferaHelper.bo.TicketType;
import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class SferaHelperTicketTypesFixer {

    private static final HashMap<TicketType, Long> italonTicketTypesMap = new HashMap<>();
    public static final int MAX_ERROR_IN_PERCENT = 5;

    //TODO
    public static final int MAX_ESTIMATION = 5*8*60*60;// 1 week

    //TODO
    public static final int MIN_ESTIMATION = 60*60;// 1 hour

    {
        italonTicketTypesMap.put(TicketType.NEW_FUNC, 40l);
        italonTicketTypesMap.put(TicketType.TECH_DEBT, 30l);
        italonTicketTypesMap.put(TicketType.ARH, 20l);
        italonTicketTypesMap.put(TicketType.DEFECT, 10l);
    }

    public static void main(String... args) throws IOException {
        String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and sprint = '4263'";
        ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

        HashMap<String, GetTicketDto> fullTicketsMap = new HashMap<>();
        HashMap<TicketType, Long> currentTicketTypesMap = new HashMap<>();
        for (TicketType ticketType : TicketType.values()) {
            currentTicketTypesMap.put(ticketType, 0l);
        }

        Long fullEstimation = 0l;
        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());
            fullTicketsMap.put(listTicketShortDto.getNumber(), ticket);
            fullEstimation += ticket.getEstimation();
            TicketType ticketType = TicketType.getTicketType(ticket);
            currentTicketTypesMap.put(ticketType, currentTicketTypesMap.get(ticketType) +  ticket.getEstimation());
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

                }
            }
        }


        System.out.println("currentTicketTypesMap = " + currentTicketTypesMap);
    }

    private static boolean match(Long diff, Long fullEstimation) {
        return (diff / fullEstimation) * 100 < MAX_ERROR_IN_PERCENT;
    }


}
