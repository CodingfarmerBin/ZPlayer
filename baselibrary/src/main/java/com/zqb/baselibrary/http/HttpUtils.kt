package com.zqb.baselibrary.http

import com.zqb.baselibrary.http.intercepter.Transformer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpUtils {
    public fun rquest(){
        val map=HashMap<String,Any>()
        val retrofit = Retrofit.Builder()
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        service.request("",Api.getRequestBody(map))
            .compose(Transformer().configSchedulers())

    }
}