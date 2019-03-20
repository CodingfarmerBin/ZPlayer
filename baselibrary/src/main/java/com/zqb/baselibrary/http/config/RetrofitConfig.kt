package com.zqb.baselibrary.http.config

import android.text.TextUtils
import com.zqb.baselibrary.base.BaseApplication
import com.zqb.baselibrary.base.Constants
import com.zqb.baselibrary.http.gson.GsonAdapter
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Retrofit配置
 */
object RetrofitConfig {

    private var retrofitBuilder = Retrofit.Builder()

    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if(retrofit==null){
            if(OkHttpConfig.getOkHttpClient()==null){
                OkHttpConfig.Builder().build()
            }
            return Builder()
                .setClient(OkHttpConfig.getOkHttpClient()!!)
                .build()
        }else {
            return retrofit!!
        }
    }



    class Builder {

        private var callAdapterFactory: Array<out CallAdapter.Factory>? = null
        private var converterFactory: Array<out Converter.Factory>? = null
        private var baseUrl:String?=null
        private var client:OkHttpClient?=null

        /**
         * 添加CallAdapterFactory
         *
         * @param factories CallAdapter.Factory
         */
        fun addCallAdapterFactory(vararg factories: CallAdapter.Factory):Builder {
            this.callAdapterFactory = factories
            return this
        }

        /**
         * 添加ConverterFactory
         *
         * @param factories Converter.Factory
         */
        fun addConverterFactory(vararg factories: Converter.Factory):Builder {
            this.converterFactory = factories
            return this
        }

        fun setBaseUrl(url:String):Builder{
            baseUrl=url
            return this
        }

        fun setClient(okHttpClient: OkHttpClient):Builder{
            client=okHttpClient
            return this
        }

        fun build(): Retrofit {
            retrofitBuilder = Retrofit.Builder()
            if (null != callAdapterFactory && callAdapterFactory!!.isNotEmpty()) {
                for (factory in callAdapterFactory!!) {
                    retrofitBuilder.addCallAdapterFactory(factory)
                }
            } else {
                retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            }
            if (null != converterFactory && converterFactory!!.isNotEmpty()) {
                for (factory in converterFactory!!) {
                    retrofitBuilder.addConverterFactory(factory)
                }
            } else {
                retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonAdapter.buildGson()))
            }
            if(client!=null) {
                retrofitBuilder.client(client!!)
            }
            retrofitBuilder.baseUrl(if(TextUtils.isEmpty(baseUrl)) Constants.BASE_URL else baseUrl!!)
            retrofit = retrofitBuilder.build()
            return retrofit!!
        }

    }
}