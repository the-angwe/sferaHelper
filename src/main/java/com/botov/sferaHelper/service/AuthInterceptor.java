package com.botov.sferaHelper.service;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class AuthInterceptor implements Interceptor {

    private final String token;

    public AuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request  = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
         return chain.proceed(authenticatedRequest);
    }
}
