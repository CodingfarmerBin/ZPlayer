package com.zqb.baselibrary.http.observer

import com.zqb.baselibrary.http.base.ApiException
import com.zqb.baselibrary.http.base.BaseSubscriber
import com.zqb.baselibrary.http.exception.ZThrowable
import io.reactivex.FlowableSubscriber
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class CommonObservable<T> : FlowableSubscriber<T> {
    override fun onComplete() {

    }

    override fun onSubscribe(s: Subscription) {

    }

    override fun onNext(t: T) {

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

    abstract fun doOnError(code: Int, msg: String?)

}