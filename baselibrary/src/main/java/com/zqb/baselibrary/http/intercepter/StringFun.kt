package com.zqb.baselibrary.http.intercepter

import android.annotation.SuppressLint
import com.zqb.baselibrary.http.gson.GsonAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import okhttp3.ResponseBody

class StringFun :Function<ResponseBody,Flowable<String>> {
    @SuppressLint("CheckResult")
    override fun apply(t: ResponseBody): Flowable<String> {
        return Flowable.create({
            val jsonString = t.string()
            it.onNext(jsonString)
        }, BackpressureStrategy.ERROR)
    }
}