package com.akundu.kkplayer.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static Retrofit retrofit;
    // BASE_URL was https://pwdown.com/11981/ its changed on 19/11/2022
    private final static String BASE_URL = "https://pwdown.info/11981/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            //okHttpClient.connectionPool();
            //okHttpClient.readTimeout(3000, TimeUnit.MILLISECONDS);
            //okHttpClient.connectTimeout(3000, TimeUnit.MILLISECONDS);
            okHttpClient.addInterceptor(interceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}