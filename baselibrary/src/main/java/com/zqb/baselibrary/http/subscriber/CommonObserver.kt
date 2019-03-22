package com.zqb.baselibrary.http.subscriber

import android.util.Log
import com.zqb.baselibrary.http.base.ApiException
import com.zqb.baselibrary.http.base.BaseObserver
import com.zqb.baselibrary.http.exception.ZThrowable
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class CommonObserver<T> : BaseObserver<T>() {
    private var disposable:Disposable ?=null
    override fun onComplete() {
        disposable?.dispose()
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable=d
        doOnSubscribe(d)
    }

    override fun onError(e: Throwable) {
        if (e is ZThrowable) {
            doOnError(e.code, e.msg)
        } else {
            doOnError(ApiException.handleException(e).code, ApiException.handleException(e).msg)
        }
    }

    override fun onNext(t: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}