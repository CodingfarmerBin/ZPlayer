package com.zqb.baselibrary.http.base

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

object Api {

    private val mediaType: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")!!

    /***
     *  创建 body
     * @return 返回RequestBody
     */
    fun getRequestBody(params: MutableMap<String, Any>?): RequestBody {
        val json:String
        if (params != null) {
            json = Gson().toJson(params)
        } else {
            json = ""
        }
        return RequestBody.create(mediaType, "parameters=$json")
    }
}