package com.botov.sferaHelper.service;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class AuthInterceptor implements Interceptor {

    private String cookie = readCookie();

    private String readCookie() {
        try {
            return cookie = new String(Files.readString(Paths.get("cookie.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request  = chain.request();
        Request authenticatedRequest = request.newBuilder().addHeader(
                "Cookie",
                cookie).build();
         return chain.proceed(authenticatedRequest);
    }
}
