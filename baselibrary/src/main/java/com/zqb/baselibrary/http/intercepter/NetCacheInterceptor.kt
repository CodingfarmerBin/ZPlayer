package com.zqb.baselibrary.http.intercepter

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

import java.io.IOException

import com.zqb.baselibrary.request.utils.NetUtils.isNetworkConnected

/**
 *
 * 网络缓存
 *
 */

class NetCacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val connected = isNetworkConnected()
        if (connected) {
            //如果有网络，缓存60s
            val response = chain.proceed(request)
            val maxTime = 60
            return response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxTime")
                .build()
        }
        //如果没有网络，不做处理，直接返回
        return chain.proceed(request)
    }

}
