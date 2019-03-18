package com.zqb.baselibrary.http.subscriber

import android.util.Log
import com.zqb.baselibrary.http.base.ApiException
import com.zqb.baselibrary.http.base.BaseSubscriber
import com.zqb.baselibrary.http.exception.ZThrowable
import org.reactivestreams.Subscription

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class CommonSubscriber<T> : BaseSubscriber<T>() {
    override fun onComplete() {

    }

    override fun onSubscribe(s: Subscription) {
        // 下游能处理几个就告诉上游要几个
        s.request(Long.MAX_VALUE)
        doOnSubscribe(s)
    }

    override fun onError(t: Throwable?) {
        if (t != null) {
            if(t is ZThrowable){
                doOnError(t.code,t.msg)
            }else {
                doOnError(ApiException.handleException(t).code, ApiException.handleException(t).msg)
            }
        }
    }



}