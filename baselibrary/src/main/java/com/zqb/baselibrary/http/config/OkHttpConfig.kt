package com.zqb.baselibrary.http.config

import android.content.Context
import android.text.TextUtils
import com.zqb.baselibrary.base.Constants
import com.zqb.baselibrary.http.Utils.SSLUtils
import com.zqb.baselibrary.http.cookie.CookieJarImpl
import com.zqb.baselibrary.http.cookie.store.CookieStore
import com.zqb.baselibrary.http.intercepter.HeaderInterceptor
import com.zqb.baselibrary.http.intercepter.LoggingInterceptor
import com.zqb.baselibrary.http.intercepter.NetCacheInterceptor
import com.zqb.baselibrary.http.intercepter.NoNetCacheInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 *
 * OKHttp配置
 */
object OkHttpConfig {

    private var defaultCachePath:String?=null
    private var defaultCacheSize = Constants.httpCacheSize
    private var defaultTimeout = Constants.httpTimeOut

    private val okHttpClientBuilder = OkHttpClient.Builder()

    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient? {
        return okHttpClient
    }

    class Builder(var context: Context) {

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

        fun setHeaders(headerMaps: Map<String, Any>): Builder {
            this.headerMaps = headerMaps
            return this
        }

        fun setDebug(isDebug: Boolean): Builder {
            this.isDebug = isDebug
            return this
        }

        fun setCache(isCache: Boolean): Builder {
            this.isCache = isCache
            return this
        }

        fun setCachePath(cachePath: String): Builder {
            this.cachePath = cachePath
            return this
        }

        fun setCacheMaxSize(cacheMaxSize: Long): Builder {
            this.cacheMaxSize = cacheMaxSize
            return this
        }

        fun setCookieType(cookieStore: CookieStore): Builder {
            this.cookieStore = cookieStore
            return this
        }

        fun setReadTimeout(readTimeout: Long): Builder {
            this.readTimeout = readTimeout
            return this
        }

        fun setWriteTimeout(writeTimeout: Long): Builder {
            this.writeTimeout = writeTimeout
            return this
        }

        fun setConnectTimeout(connectTimeout: Long): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        fun setAddInterceptor(vararg interceptors: Interceptor): Builder {
            this.interceptors = interceptors
            return this
        }

        fun setSslSocketFactory(vararg certificates: InputStream): Builder {
            this.certificates = certificates
            return this
        }

        fun setSslSocketFactory(bksFile: InputStream, password: String, vararg certificates: InputStream): Builder {
            this.bksFile = bksFile
            this.password = password
            this.certificates = certificates
            return this
        }


        fun build(): OkHttpClient {

            setCookieConfig()
//            setCacheConfig()
            setHeadersConfig()
            setSslConfig()
            addInterceptors()
            setTimeout()
            setDebugConfig()

            okHttpClient = okHttpClientBuilder.build()
            return okHttpClient!!
        }

        private fun addInterceptors() {
            if (null != interceptors) {
                for (interceptor in interceptors!!) {
                    okHttpClientBuilder.addInterceptor(interceptor)
                }
            }
        }

        /**
         * 配置开发环境
         */
        private fun setDebugConfig() {
            if (isDebug) {
                val logInterceptor = HttpLoggingInterceptor(LoggingInterceptor())
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClientBuilder.addInterceptor(logInterceptor)
            }
        }


        /**
         * 配置headers
         */
        private fun setHeadersConfig() {
            okHttpClientBuilder.addInterceptor(HeaderInterceptor(headerMaps))
        }

        /**
         * 配置headers
         */
        private fun setHeadersConfig(map:HashMap<String, Any>) {
            headerMaps=map
            okHttpClientBuilder.addInterceptor(HeaderInterceptor(map))
        }

        /**
         * 配饰cookie保存到sp文件中
         */
        private fun setCookieConfig() {
            if (null != cookieStore) {
                okHttpClientBuilder.cookieJar(CookieJarImpl(cookieStore))
            }
        }

        /**
         * 配置缓存
         */
        private fun setCacheConfig() {
            val externalCacheDir = context.externalCacheDir ?: return
            defaultCachePath = externalCacheDir.path + "/RxHttpCacheData"
            if (isCache) {
                val cache: Cache
                if (!TextUtils.isEmpty(cachePath) && cacheMaxSize > 0) {
                    cache = Cache(File(cachePath), cacheMaxSize)
                } else {
                    cache = Cache(File(defaultCachePath), defaultCacheSize)
                }
                okHttpClientBuilder
                    .cache(cache)
                    .addInterceptor(NoNetCacheInterceptor())
                    .addNetworkInterceptor(NetCacheInterceptor())
            }
        }

        /**
         * 配置超时信息
         */
        private fun setTimeout() {
            okHttpClientBuilder.readTimeout(if (readTimeout == 0L) defaultTimeout else readTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.writeTimeout(if (writeTimeout == 0L) defaultTimeout else writeTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.connectTimeout(
                if (connectTimeout == 0L) defaultTimeout else connectTimeout,
                TimeUnit.SECONDS
            )
            okHttpClientBuilder.retryOnConnectionFailure(true)
        }

        /**
         * 配置证书
         */
        private fun setSslConfig() {
            val sslParams: SSLUtils.SSLParams
            if (null == certificates) {
                //信任所有证书,不安全有风险
                sslParams = SSLUtils.getSslSocketFactory()
            } else {
                if (null != bksFile && !TextUtils.isEmpty(password)) {
                    //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(bksFile!!, password!!, *certificates!!)
                } else {
                    //使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(*certificates!!)
                }
            }

            okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!)

        }
    }
}