package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.ListTicketsDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface SferaService {

    @GET("app/tasks/api/v0.1/entities")
    Call<ListTicketsDto> listTicketsByQuery(@Query("query") String query);

}
