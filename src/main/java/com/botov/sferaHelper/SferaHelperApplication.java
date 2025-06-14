package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketShortDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.service.SferaHelperMethods;

import java.io.IOException;

class SferaHelperApplication {
		public static void main(String... args) throws IOException {
			//String query = "area='RDS'";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation=null";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent=null";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1553 Заявки ФЛ\"";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1672_3 Аутентификация подтверждение операций\"";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer')";
			//String query = "area=\"FRNRSA\" and updateDate > '2024-09-30'";
			String query = "area=\"FRNRSA\" and number='FRNRSA-8083'";
			//String query =  "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1672_3 Аутентификация подтверждение операций\"";

			//String query =  "area=\"FRNRSA\" and parent in ('STROMS-3582', 'STROMS-2384', 'STROMS-3761', 'STROMS-3664') and name ~ 'еженедельные проверки'";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent='STROMS-4007'";


			ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

			for (ListTicketShortDto ticket: listTicketsDto.getContent()) {
				//SferaHelperMethods.setSystem(ticket, "\"1672_3 Аутентификация подтверждение операций\"");
				//SferaHelperMethods.setParent(ticket.getNumber(), "STROMS-4062");
				//SferaHelperMethods.setEstimation(ticket.getNumber(), 3600L);
				//SferaHelperMethods.setDueDate(ticket.getNumber(), "2025-06-30");
				//SferaHelperMethods.setProject(ticket.getNumber(), "f9696ccf-0f8d-431e-a803-9d00ee6e3329");// проект 2973
				//SferaHelperMethods.setSystem(ticket.getNumber(), "\"1553 Заявки ФЛ\"");
				//if (ticket.getSystems() != null && !ticket.getSystems().isEmpty() && !ticket.getSystems().contains("1553 Заявки ФЛ")) {
				//SferaHelperMethods.setSystem(ticket.getNumber(), "1553 Заявки ФЛ");
				//}
				SferaHelperMethods.setSprint(ticket.getNumber(), null);

			}
			System.out.println("end");
		}

}
