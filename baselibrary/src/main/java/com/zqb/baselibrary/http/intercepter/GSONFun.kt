package com.zqb.baselibrary.http.intercepter

import android.annotation.SuppressLint
import com.zqb.baselibrary.http.gson.GsonAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.functions.Function
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.lang.reflect.ParameterizedType
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




/**
 *
 * Gson解析
 */
class GSONFun<T>(private val responseClass:Class<T>?):Function<String,Flowable<T>> {

    @SuppressLint("CheckResult")
    override fun apply(t: String): Flowable<T> {
        return Flowable.create({
            if(responseClass!=null) {
                val fromJson = GsonAdapter.buildGson().fromJson(t, responseClass)
                it.onNext(fromJson)
            }else{
                it.onNext(t as T)
            }
        }, BackpressureStrategy.BUFFER)
    }



}