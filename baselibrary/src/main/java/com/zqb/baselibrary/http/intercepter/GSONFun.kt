package com.zqb.baselibrary.http.intercepter

import android.annotation.SuppressLint
import com.zqb.baselibrary.http.gson.GsonAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import okhttp3.ResponseBody

class GSONFun<T>(private val responseClass:Class<T>?):Function<ResponseBody,Flowable<T>> {
    @SuppressLint("CheckResult")
    override fun apply(t: ResponseBody): Flowable<T> {
        return Flowable.create({
            val jsonString = t.string()
            val json = GsonAdapter.buildGson().fromJson<T>(jsonString,responseClass)
            it.onNext(json)
        }, BackpressureStrategy.ERROR)
    }
}