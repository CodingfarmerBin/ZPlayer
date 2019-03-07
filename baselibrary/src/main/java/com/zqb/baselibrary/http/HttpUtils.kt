package com.zqb.baselibrary.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpUtils {
    public fun rquest(){
        var retrofit = Retrofit.Builder()
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var service = retrofit.create(ApiService::class.java)
    }
}