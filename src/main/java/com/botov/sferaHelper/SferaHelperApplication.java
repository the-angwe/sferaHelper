package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketDto;
import com.botov.sferaHelper.service.SferaHelperMethods;
import com.botov.sferaHelper.service.SferaService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

class SferaHelperApplication {
		public static void main(String... args) throws IOException {
			//String query = "area='RDS'";
			String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and estimation=null";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and parent=null";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1553 Заявки ФЛ\"";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1672_3 Аутентификация подтверждение операций\"";
			//String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer')";
			ListTicketsDto listTicketsDto = SferaHelperMethods.listTicketsByQuery(query);

			for (TicketDto ticket: listTicketsDto.getContent()) {
				//SferaHelperMethods.setSystem(ticket, "\"1672_3 Аутентификация подтверждение операций\"");
				//SferaHelperMethods.setParent(ticket, "STROMS-3199");
				SferaHelperMethods.setEstimation(ticket, 3600L);
				//SferaHelperMethods.setDueDate(ticket, "2025-03-31");
			}
			System.out.println("end");
		}

}
