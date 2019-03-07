package com.zqb.baselibrary.http

import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService{
    @POST
    fun request(@Url url:String, @Body body: RequestBody): Flowable<String>
}
