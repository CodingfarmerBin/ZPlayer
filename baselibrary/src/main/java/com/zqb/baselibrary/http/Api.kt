package com.zqb.baselibrary.http

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

object Api {

    private val mediaType: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")!!

    /***
     *  创建 body
     * @return 返回RequestBody
     */
    fun getRequestBody(params: MutableMap<String, Any>): RequestBody {
        val json = Gson().toJson(params)
        return RequestBody.create(mediaType, "parameters=$json")
    }
}