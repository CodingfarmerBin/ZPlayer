package com.zqb.baselibrary.http.intercepter

import okhttp3.CacheControl
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

class NoNetCacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val connected = isNetworkConnected()
        //如果没有网络，则启用 FORCE_CACHE
        if (!connected) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

            val response = chain.proceed(request)

            //没网的时候如果也没缓存的话就走网络
            if (response.code() == 504) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()
                return chain.proceed(request)
            }

            return response.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=3600")
                .removeHeader("Pragma")
                .build()
        }
        //有网络的时候，这个拦截器不做处理，直接返回
        return chain.proceed(request)
    }

}
