package com.zqb.baselibrary.http.intercepter

import android.annotation.SuppressLint
import android.util.Log
import com.zqb.baselibrary.http.base.ApiException
import com.zqb.baselibrary.http.exception.ZThrowable
import com.zqb.baselibrary.http.gson.GsonAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import okhttp3.ResponseBody

class GSONFun<T>:Function<ResponseBody,Flowable<T>> {
    @SuppressLint("CheckResult")
    override fun apply(t: ResponseBody): Flowable<T> {
        return Flowable.create({
            Log.d("haha","${t.contentLength()}")
            if(t.contentLength()<=0){
                val zThrowable = ZThrowable()
                zThrowable.code=ApiException.ERROR.UNKNOWN
                zThrowable.msg="未知错误"
                it.onError(zThrowable)
            }else {
                val jsonString = t.string()
                val json = GsonAdapter.buildGson().fromJson<T>(jsonString, javaClass)
                it.onNext(json)
            }
        }, BackpressureStrategy.ERROR)
    }
}