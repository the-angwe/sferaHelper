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

        HashMap<TicketType, List<GetTicketDto>> fullTicketsMap = new HashMap<>();

        for (ListTicketShortDto listTicketShortDto: listTicketsDto.getContent()) {
            GetTicketDto ticket = SferaHelperMethods.ticketByNumber(listTicketShortDto.getNumber());

            ensureEstimation(ticket);
            TicketType ticketType = TicketType.getTicketType(ticket);

            List<GetTicketDto> fullTicketsMapSet = fullTicketsMap.get(ticketType);
            if (fullTicketsMapSet == null) {
                fullTicketsMapSet = new ArrayList<>();
                fullTicketsMap.put(ticketType, fullTicketsMapSet);
            }
            fullTicketsMapSet.add(ticket);
        }

        long fullEstimation = calcFullEstimation(fullTicketsMap);

        //fix bugs estimations
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
                        //TODO restore it
                        //SferaHelperMethods.setEstimation(ticket.getNumber(), estimation);
                    }
                }
            }
        }

        //remove zero italons
        for (TicketType ticketType : TicketType.values()) {
            if (!ticketType.isCanChange()) {
                continue;
            }
            long italon = calcItalonEstimation(ticketType, fullEstimation);
            if (italon == 0) {
                TicketType newTicketType = maximumItalonTicketType();
                Iterator<GetTicketDto> donorTickets = fullTicketsMap.get(ticketType).iterator();
                while (donorTickets.hasNext()) {
                    GetTicketDto donorTicket = donorTickets.next();
                    changeType(donorTicket, fullTicketsMap, newTicketType);
                    donorTickets.remove();
                }
            }
        }

        //refresh after last step
        fullEstimation = calcFullEstimation(fullTicketsMap);

        for (TicketType ticketType : TicketType.values()) {
            if (!ticketType.isCanChange()) {
                continue;
            }
            long italon = calcItalonEstimation(ticketType, fullEstimation);
            if (italon==0) {
                continue;
            }
            Long curr = calcEstimation(ticketType, fullTicketsMap);
            long diff = italon - curr;
            if (match(diff, fullEstimation)) {
                continue;
            }
            for (TicketType donorTicketType : TicketType.values()) {
                if (!donorTicketType.isCanChange() || donorTicketType==ticketType) {
                    continue;
                }
                List<GetTicketDto> donorTickets = fullTicketsMap.get(donorTicketType);
                Collections.sort(donorTickets, (o1, o2) -> o2.getEstimation().compareTo(o1.getEstimation()));
                Iterator<GetTicketDto> it = donorTickets.iterator();
                while (it.hasNext() && diff > MIN_ESTIMATION_STEP) {
                    GetTicketDto donorTicket = it.next();
                    if (donorTicket.getEstimation() > diff) {
                        continue;
                    }
                    diff -= donorTicket.getEstimation();
                    changeType(donorTicket, fullTicketsMap, ticketType);
                    it.remove();
                }
            }
        }

        for (TicketType ticketType : TicketType.values()) {
            Long italon = calcItalonEstimation(ticketType, fullEstimation);
            Long curr = calcEstimation(ticketType, fullTicketsMap);
            long diff = italon - curr;
            System.out.println("For " + ticketType + ": italon=" + italon + "; curr=" + curr + "; diff=" + diff);
        }

        System.out.println("fullTicketsMap = " + fullTicketsMap);
    }

    private static TicketType maximumItalonTicketType() {
        TicketType result = null;
        long max = 0l;
        for (Map.Entry<TicketType, Long> entry : italonTicketTypesMap.entrySet()) {
            long value = entry.getValue().longValue();
            if (value > max) {
                result = entry.getKey();
            }
        }
        return result;
    }

    private static void changeType(GetTicketDto donorTicket, HashMap<TicketType, List<GetTicketDto>> fullTicketsMap, TicketType ticketType) throws IOException {
        fullTicketsMap.get(ticketType).add(donorTicket);
        SferaHelperMethods.setTicketType(donorTicket.getNumber(), ticketType);
    }

    private static long calcItalonEstimation(TicketType ticketType, long fullEstimation) {
        return Integer.valueOf(Math.round((italonTicketTypesMap.getOrDefault(ticketType, 0L)*fullEstimation)/100L)).longValue();
    }

    private static long calcFullEstimation(HashMap<TicketType, List<GetTicketDto>> fullTicketsMap) {
        long result = 0l;
        for (TicketType ticketType : fullTicketsMap.keySet()) {
            result += calcEstimation(ticketType, fullTicketsMap);
        }
        return result;
    }

    private static long calcEstimation(TicketType ticketType, HashMap<TicketType, List<GetTicketDto>> fullTicketsMap) {
        long result = 0l;
        List<GetTicketDto> tickets = fullTicketsMap.get(ticketType);
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
