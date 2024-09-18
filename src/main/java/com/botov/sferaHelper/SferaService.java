package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.TicketDto;
import retrofit2.Call;
import retrofit2.http.*;

public interface SferaService {

    @GET("app/tasks/api/v0.1/entities")
    Call<ListTicketsDto> listTicketsByQuery(@Query("query") String query, @Query("size") int size);

    @PATCH("app/tasks/api/v0.1/entities/{number}")
    Call<Void> patchTicket(@Path("number") String number, @Body TicketDto estimation);

}
