package com.skinfotech.ekinch.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitApi {

    private RetrofitApi() {
        /*
         * This is done just to satisfy the sonar
         * */
    }

    private static final String BASE_URL = "https://ekinch.in/api/";

    private static AppServices sAppServices = null;

    public static AppServices getAppServicesObject() {
        if (null == sAppServices) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS)
                                                            .addInterceptor(interceptor).protocols(Util.immutableListOf(Protocol.HTTP_1_1)).build();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
            sAppServices = retrofit.create(AppServices.class);
        }
        return sAppServices;
    }
}
