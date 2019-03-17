package com.zqb.baselibrary.http.base

import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService{
    @POST
    fun <T> post(@Url url:String, @Body body: RequestBody): Flowable<T>

    @GET
    fun get(@Url url:String, @Body body: RequestBody): Flowable<String>

    @Multipart
    @POST
    fun upload(@Url url:String,@Part files: List<MultipartBody.Part>): Flowable<String>

}
