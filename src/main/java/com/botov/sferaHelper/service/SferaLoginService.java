package com.botov.sferaHelper.service;

import com.botov.sferaHelper.dto.*;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.util.List;

public interface SferaLoginService {

    SferaLoginService INSTANCE = createSferaService();

    private static SferaLoginService createSferaService() {
        var client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sfera.inno.local/")
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .client(client)
                .build();

        return retrofit.create(SferaLoginService.class);
    }

    @POST("api/auth/login")
    Call<AuthTokenDto> login(@Body AuthRequestDto requestDto);

}
