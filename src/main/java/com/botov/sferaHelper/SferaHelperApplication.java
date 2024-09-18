package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketDto;
import com.botov.sferaHelper.service.SferaService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

class SferaHelperApplication {
		public static void main(String... args) throws IOException {
			//String query = "area='RDS'";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1672 Аутентификация ФЛ РБ\" and parent = null";
			String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation = null";
			ListTicketsDto listTicketsDto = listTicketsByQuery(query);

			for (TicketDto ticket: listTicketsDto.getContent()) {
				//setSystem(ticket, "\"1672_3 Аутентификация подтверждение операций\"");
				//setParent(ticket, "STROMS-2723");
				setEstimation(ticket, 3600L);
			}
			System.out.println("end");
		}

	private static ListTicketsDto listTicketsByQuery(String query) throws IOException {
		var response = SferaService.INSTANCE.listTicketsByQuery(query, 100).execute();
		System.out.println("response=" + response);
		System.out.println("response.body()=" + response.body());

		return response.body();
	}

	private static void setEstimation(TicketDto ticket, long estimation) throws IOException {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setEstimation(estimation);
		patchTicket(ticket.getNumber(), ticketDto);
	}

	private static void setParent(TicketDto ticket, String parent) throws IOException {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setParent(parent);
		patchTicket(ticket.getNumber(), ticketDto);
	}

	//TODO not working
	private static void setSystem(TicketDto ticket, String system) throws IOException {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setSystems(Collections.singleton(system));
		patchTicket(ticket.getNumber(), ticketDto);
	}

	private static void patchTicket(String number, TicketDto ticketDto) throws IOException {
		System.out.println("patch " + number + " with " + ticketDto);
		SferaService.INSTANCE.patchTicket(number, ticketDto).execute();
	}

}
