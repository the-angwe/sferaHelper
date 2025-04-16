package com.botov.sferaHelper.service;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request  = chain.request();
        String log = "request = " + request.method() + " "
                + request.url();
        if (request.body()!=null) {
            log = log + "; body="+ bodyToString(request);
        }
        System.out.println(log);
        return chain.proceed(request);
    }

    private static String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
