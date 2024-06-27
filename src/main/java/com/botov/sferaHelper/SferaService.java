package com.botov.sferaHelper;

import com.botov.sferaHelper.dto.EstimationDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SferaService {

    @GET("app/tasks/api/v0.1/entities")
    Call<ListTicketsDto> listTicketsByQuery(@Query("query") String query, @Query("size") int size);

    @PATCH("app/tasks/api/v0.1/entities/{number}")
    Call<Void> setTicketEstimation(@Path("number") String number, @Body EstimationDto estimation);

}
