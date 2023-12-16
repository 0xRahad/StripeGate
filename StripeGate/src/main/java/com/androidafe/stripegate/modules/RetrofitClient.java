package com.androidafe.stripegate.modules;

import com.androidafe.stripegate.interfaces.RetrofitInstance;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private String URL = "https://api.stripe.com/";
    private static RetrofitClient client;
    private static Retrofit retrofit;

    RetrofitClient() {
        retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RetrofitClient getInstance(){
        if (client == null){
            client = new RetrofitClient();
        }
        return client;

    }

    public RetrofitInstance getAPI(){
        return retrofit.create(RetrofitInstance.class);
    }

}
