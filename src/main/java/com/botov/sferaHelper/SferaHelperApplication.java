package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketDto;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

class SferaHelperApplication {
		public static void main(String... args) throws IOException {
			SferaService sferaService = createSferaService();
			//String query = "area='RDS'";
			String query = "area=\"FRNRSA\" and status not in ('closed', 'done', 'rejectedByThePerformer') and systems = \"1672_3 Аутентификация подтверждение операций\"";

			ListTicketsDto listTicketsDto = listTicketsByQuery(sferaService, query);

			for (TicketDto ticket: listTicketsDto.getContent()) {
				setParent(sferaService, ticket, "STROMS-2722");
			}

			/*for (TicketDto ticket: listTicketsDto.getContent()) {
				setEstimation(sferaService, ticket, 3600L);
			}*/
			System.out.println("end");
		}

	private static ListTicketsDto listTicketsByQuery(SferaService sferaService, String query) throws IOException {
		var response = sferaService.listTicketsByQuery(query, 100).execute();
		System.out.println("response=" + response);
		System.out.println("response.body()=" + response.body());

		return response.body();
	}

	private static SferaService createSferaService() {
		var client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://sfera.inno.local/")
				.addConverterFactory(GsonConverterFactory.create())
				.client(client)
				.build();

		return retrofit.create(SferaService.class);
	}

	private static void setEstimation(SferaService sferaService, TicketDto ticket, long estimation) throws IOException {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setEstimation(estimation);
		System.out.println("set estimation for " + ticket.getNumber());
		sferaService.patchTicket(ticket.getNumber(), ticketDto).execute();
	}

	private static void setParent(SferaService sferaService, TicketDto ticket, String parent) throws IOException {
		TicketDto ticketDto = new TicketDto();
		ticketDto.setParent(parent);
		System.out.println("set parent for " + ticket.getNumber());
		sferaService.patchTicket(ticket.getNumber(), ticketDto).execute();
	}

}
