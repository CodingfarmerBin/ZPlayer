package com.zqb.baselibrary.http.intercepter

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

import java.io.IOException
import java.util.TreeMap

/**
 * 请求拦截器  统一添加请求头使用
 */

class HeaderInterceptor(var headerMaps: Map<String, Any>) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("Content-Type", "text/html; charset=UTF-8")
        if (headerMaps.isNotEmpty()) {
            for ((key, value) in headerMaps) {
                request.addHeader(key, value.toString())
            }
        }
        return chain.proceed(request.build())
    }

}
