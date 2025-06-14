package com.botov.sferaHelper.service;

import com.botov.sferaHelper.dto.AttributesDto;
import com.botov.sferaHelper.dto.GetTicketDto;
import com.botov.sferaHelper.dto.ListTicketsDto;
import com.botov.sferaHelper.dto.PatchTicketDto;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public interface SferaService {

    SferaService INSTANCE = createSferaService();

    private static SferaService createSferaService() {
        var client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sfera.inno.local/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(SferaService.class);
    }

    @GET("app/tasks/api/v0.1/entities")
    Call<ListTicketsDto> listTicketsByQuery(@Query("query") String query, @Query("size") int size, @Query("page") int page);

    @PATCH("app/tasks/api/v0.1/entities/{number}")
    Call<Void> patchTicket(@Path("number") String number, @Body PatchTicketDto estimation);

    @PATCH("app/tasks/api/v1/entities/{number}")
    Call<Void> patchTicket2(@Path("number") String number, @Body PatchTicketDto estimation);

    @GET("app/tasks/api/v1/entity-views/{number}")
    Call<GetTicketDto> getTicket(@Path("number") String number);

    @HTTP(method = "DELETE", path = "app/tasks/api/v1/entities/attributes", hasBody = true)
    Call<Void> deleteAttributes(@Body AttributesDto attributes);

}
