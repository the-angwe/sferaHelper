package com.botov.sferaHelper.service;

import com.botov.sferaHelper.dto.*;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.util.List;

public interface SferaService {

    @GET("app/tasks/api/v0.1/entities")
    Call<ListTicketsDto> listTicketsByQuery(@Query("query") String query, @Query("size") int size, @Query("page") int page);

    @GET("app/tasks/api/v1/sprints")
    Call<ListSprintDto> listSprints(@Query("areaCodes") String areaCode, @Query("keyword") String keyword, @Query("size") int size, @Query("page") int page);

    @PATCH("app/tasks/api/v0.1/entities/{number}")
    Call<Void> patchTicket(@Path("number") String number, @Body PatchTicketDto estimation);

    @PATCH("app/tasks/api/v1/entities/{number}")
    Call<Void> patchTicket2(@Path("number") String number, @Body PatchTicketDto estimation);

    @GET("app/tasks/api/v1/entity-views/{number}")
    Call<GetTicketDto> getTicket(@Path("number") String number);

    @HTTP(method = "DELETE", path = "app/tasks/api/v1/entities/attributes", hasBody = true)
    Call<Void> deleteAttributes(@Body List<AttributesDto> attributes);

    @POST("app/tasks/api/v1/entities/copy")
    Call<TicketCopyResponseDto> copyTicket(@Body TicketCopyRequestDto requestDto);

}
