package com.botov.sferaHelper.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SferaServiceImpl {
    public static SferaService INSTANCE = null;

    public static SferaService createSferaService(String token) {
        var client = UnsafeOkHttpClient.getAuthenticatedUnsafeOkHttpClient(token);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sfera.inno.local/")
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .client(client)
                .build();

        return retrofit.create(SferaService.class);
    }

}
