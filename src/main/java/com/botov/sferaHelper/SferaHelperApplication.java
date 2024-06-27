package com.botov.sferaHelper;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

class SferaHelperApplication {
		public static void main(String... args) throws IOException {

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("https://sfera.inno.local/")
					.addConverterFactory(GsonConverterFactory.create())
					.client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
					.build();

			SferaService sferaService = retrofit.create(SferaService.class);
			var a = sferaService.listTicketsByQuery("area='RDS'");
			//var a = sferaService.listTicketsByQuery();
			Response<String> b = a.execute();
			System.out.println("test1");
		}

}
