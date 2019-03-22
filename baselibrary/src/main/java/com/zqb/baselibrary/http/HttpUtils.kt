package com.zqb.baselibrary.http

import android.annotation.SuppressLint
import com.zqb.baselibrary.http.base.Api
import com.zqb.baselibrary.http.base.ApiService
import com.zqb.baselibrary.http.config.OkHttpConfig
import com.zqb.baselibrary.http.config.RetrofitConfig
import com.zqb.baselibrary.http.cookie.CookieJarImpl
import com.zqb.baselibrary.http.cookie.store.CookieStore
import com.zqb.baselibrary.http.intercepter.GSONFun
import com.zqb.baselibrary.http.request.IRequest
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Retrofit

class HttpUtils {
    @SuppressLint("StaticFieldLeak")
    companion object {
        //双重校验锁式 单例
        val instance: HttpUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpUtils()
        }

        /**
         * post请求
         */
        fun <T> post(url: String, map: HashMap<String, Any>?,responseClass:Class<T>): Observable<T> {
            return instance
                .config()
                .create(ApiService::class.java)
                .post(url, Api.getRequestBody(map))
                .flatMap(GSONFun(responseClass))
        }


        /**
         * post请求 返回参数String类型
         */
        fun  post(url: String, map: HashMap<String, Any>?): Observable<String> {
            return instance
                .config()
                .create(ApiService::class.java)
                .post(url, Api.getRequestBody(map))
        }

        /**
         * get请求
         */
        fun <T> get(url: String,responseClass:Class<T>): Observable<T> {
            return instance
                .config()
                .create(ApiService::class.java)
                .get(url)
                .flatMap(GSONFun(responseClass))
        }

        /**
         * get请求 通过自定义的参数
         */
        fun <T> get(data: IRequest,responseClass:Class<T>): Observable<T> {
            return instance
                .config()
                .create(ApiService::class.java)
                .get(data.url!!)
                .flatMap(GSONFun(responseClass))
        }


        /**
         *  上传
         */
        fun upload(url: String, list: List<MultipartBody.Part>): HttpUtils {
            instance
                .config()
                .create(ApiService::class.java)
                .upload(url, list)
            return instance
        }

        fun <T> createApi(service: Class<T>): T {
            return instance
                .config()
                .create(service)
        }
    }

    /**
     * 配置 OKHttpClient 和
     */
    private fun config(): Retrofit {
        return RetrofitConfig.getRetrofit()
    }

    /**
     * 获取全局的CookieJarImpl实例
     */
    private fun getCookieJar(): CookieJarImpl {
        return OkHttpConfig.getOkHttpClient()!!.cookieJar() as CookieJarImpl
    }

    /**
     * 获取全局的CookieStore实例
     */
    private fun getCookieStore(): CookieStore? {
        return getCookieJar().cookieStore
    }

    /**
     * 获取所有cookie
     */
    fun getAllCookie(): List<Cookie>? {
        val cookieStore = getCookieStore()
        return cookieStore?.allCookie
    }

    /**
     * 获取某个url所对应的全部cookie
     */
    fun getCookieByUrl(url: String): List<Cookie>? {
        val cookieStore = getCookieStore()
        val httpUrl = HttpUrl.parse(url)
        return cookieStore?.getCookie(httpUrl!!)
    }


    /**
     * 移除全部cookie
     */
    fun removeAllCookie() {
        val cookieStore = getCookieStore()
        cookieStore?.removeAllCookie()
    }

    /**
     * 移除某个url下的全部cookie
     */
    fun removeCookieByUrl(url: String) {
        val httpUrl = HttpUrl.parse(url)
        val cookieStore = getCookieStore()
        cookieStore?.removeCookie(httpUrl!!)
    }

}