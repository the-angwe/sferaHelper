package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketCopyResponseDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;

class SferaHelperTicketCopier {
		public static void main(String... args) throws IOException {
			String[] sprints = new String[] {"4350", "4351", "4352", "4353", "4354", "4355", "4356"};
			String[] ticketNumbers = new String[] {"FRNRSA-9015"};

			for (String ticketNumber : ticketNumbers) {
				GetTicketDto ticket = SferaHelperMethods.ticketByNumber(ticketNumber);
				for (String sprint : sprints) {
					String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer')";
					query = query + " and name = '" + ticket.getName() + "'";
					query = query + " and assignee in (\"" + ticket.getAssignee().getIdentifier() + "\")";
					query = query + " and sprint = '" + sprint + "'";
					ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);
					if (listTicketsDto.getContent().size() > 0) {
						System.err.println("Ticket '" + ticket.getName() + "' already in sprint " + sprint);
						continue;
					}
					//Copy ticket
					TicketCopyResponseDto ticketCopy = SferaHelperMethods.copyTicket(ticket);
					SferaHelperMethods.setSprint(ticketCopy.getNumber(), sprint);
					SferaHelperMethods.setEstimation(ticketCopy.getNumber(), ticket.getEstimation());
					System.err.println("Ticket '" + ticket.getName() + "' created in sprint " + sprint);
				}
			}
			System.out.println("end");
		}

}
