package com.rahad.stripegate

import com.rahad.stripegate.apis.SystemAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    fun getApiInterface() : SystemAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SystemAPI::class.java)
    }
}