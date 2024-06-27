package com.botov.sferaHelper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface SferaService {

    //@GET("app/tasks/api/v0.1/entities?query={query}")
    @GET("app/tasks/api/v0.1/entities")
    Call<String> listTicketsByQuery(@Query("query") String query);

/*    @GET("app/tasks/")
    Call<List<String>> listTicketsByQuery();*/

}
