package com.greyeg.tajr.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseClient {
    private static Retrofit retrofit = null;

    private static OkHttpClient buildClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public static Retrofit getBaseClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("https://tajr.xyz/")
                    .build();
        }
        return retrofit;
    }

    public static Api getService(){
        return getBaseClient().create(Api.class);
    }

}
