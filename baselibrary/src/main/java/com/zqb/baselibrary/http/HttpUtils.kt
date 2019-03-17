package com.zqb.baselibrary.http

import android.annotation.SuppressLint
import com.zqb.baselibrary.base.BaseApplication
import com.zqb.baselibrary.base.Constants
import com.zqb.baselibrary.http.base.Api
import com.zqb.baselibrary.http.base.ApiService
import com.zqb.baselibrary.http.config.OkHttpConfig
import com.zqb.baselibrary.http.config.RetrofitConfig
import com.zqb.baselibrary.http.cookie.CookieJarImpl
import com.zqb.baselibrary.http.cookie.store.CookieStore
import com.zqb.baselibrary.http.request.IRequest
import io.reactivex.Flowable
import okhttp3.*
import retrofit2.Retrofit

class HttpUtils {

    @SuppressLint("StaticFieldLeak")
    private var instance: HttpUtils? = null

    fun getInstance(): HttpUtils {
        if (instance == null) {
            synchronized(HttpUtils::class.java) {
                if (instance == null) {
                    instance = HttpUtils()
                }
            }

        }
        return instance!!
    }

    /**
     * get请求
     */
    fun get(url: String,map:HashMap<String,Any>?):HttpUtils{
        getInstance()
            .config()
            .create(ApiService::class.java)
            .get(url, Api.getRequestBody(map))
        return this
    }

    /**
     * get请求 通过自定义的参数
     */
    fun get(data:IRequest):HttpUtils{
        get(data.url!!,data.map)
        return this
    }

    /**
     * post请求
     */
    fun<T> post(url: String,map:HashMap<String,Any>?):Flowable<T>{
        return  getInstance()
            .config()
            .create(ApiService::class.java)
            .post(url, Api.getRequestBody(map))
    }

    /**
     *  上传
     */
    fun upload(url: String,list:List<MultipartBody.Part>):HttpUtils{
        getInstance()
            .config()
            .create(ApiService::class.java)
            .upload(url,list)
        return this
    }

    /**
     * 配置 OKHttpClient 和
     */
    private fun config():Retrofit{
        return RetrofitConfig.Builder()
            .setBaseUrl(Constants.BASE_URL)
            .setClient(OkHttpConfig.Builder(BaseApplication()).build())
            .build()
    }

    fun <T> createApi (service:Class<T> ):T{
        return config().create(service)
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