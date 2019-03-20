package com.zqb.baselibrary.http.config

import com.zqb.baselibrary.base.BaseApplication
import com.zqb.baselibrary.http.cookie.store.CookieStore
import com.zqb.baselibrary.http.cookie.store.SPCookieStore
import com.zqb.baselibrary.http.intercepter.HeaderInterceptor
import okhttp3.Interceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import java.io.InputStream

class HttpConfig {

    private var headerMaps: Map<String, Any> = HashMap()
    private var isDebug: Boolean = false
    private var isCache: Boolean = false
    private var cachePath: String? = null
    private var cacheMaxSize: Long = 0
    private var cookieStore: CookieStore? = null
    private var readTimeout: Long = 0
    private var writeTimeout: Long = 0
    private var connectTimeout: Long = 0
    private var bksFile: InputStream? = null
    private var password: String? = null
    private var certificates: Array<out InputStream>? = null //out 修饰符  形变注解 声明处形变
    private var interceptors: Array<out Interceptor>? = null

    private var callAdapterFactory: Array<out CallAdapter.Factory>? = null
    private var converterFactory: Array<out Converter.Factory>? = null
    private var baseUrl: String? = null

    /**
     * 添加CallAdapterFactory
     *
     * @param factories CallAdapter.Factory
     */
    fun addCallAdapterFactory(vararg factories: CallAdapter.Factory): HttpConfig {
        this.callAdapterFactory = factories
        return this
    }

    /**
     * 添加ConverterFactory
     *
     * @param factories Converter.Factory
     */
    fun addConverterFactory(vararg factories: Converter.Factory): HttpConfig {
        this.converterFactory = factories
        return this
    }

    fun setBaseUrl(url: String): HttpConfig {
        baseUrl = url
        return this
    }

    fun setHeaders(headerMaps: Map<String, Any>): HttpConfig {
        this.headerMaps=headerMaps
        OkHttpConfig.okHttpClientBuilder.interceptors().remove(HeaderInterceptor(headerMaps))
        OkHttpConfig.okHttpClientBuilder.addInterceptor(HeaderInterceptor(headerMaps))
        return this
    }

    fun setDebug(isDebug: Boolean): HttpConfig {
        this.isDebug = isDebug
        return this
    }

    fun setCache(isCache: Boolean): HttpConfig {
        this.isCache = isCache
        return this
    }

    fun setCachePath(cachePath: String): HttpConfig {
        this.cachePath = cachePath
        return this
    }

    fun setCacheMaxSize(cacheMaxSize: Long): HttpConfig {
        this.cacheMaxSize = cacheMaxSize
        return this
    }

    fun setCookieType(cookieStore: CookieStore): HttpConfig {
        this.cookieStore = cookieStore
        return this
    }

    fun setReadTimeout(readTimeout: Long): HttpConfig {
        this.readTimeout = readTimeout
        return this
    }

    fun setWriteTimeout(writeTimeout: Long): HttpConfig {
        this.writeTimeout = writeTimeout
        return this
    }

    fun setConnectTimeout(connectTimeout: Long): HttpConfig {
        this.connectTimeout = connectTimeout
        return this
    }

    fun setAddInterceptor(vararg interceptors: Interceptor): HttpConfig {
        this.interceptors = interceptors
        return this
    }

    fun setSslSocketFactory(vararg certificates: InputStream): HttpConfig {
        this.certificates = certificates
        return this
    }

    fun setSslSocketFactory(bksFile: InputStream, password: String, vararg certificates: InputStream): HttpConfig {
        this.bksFile = bksFile
        this.password = password
        this.certificates = certificates
        return this
    }

    fun config() {
        if (OkHttpConfig.getOkHttpClient() != null) {
            OkHttpConfig.Builder()
                .build()
        }
    }
}