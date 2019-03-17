package com.zqb.baselibrary.http.base

import io.reactivex.FlowableSubscriber
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class BaseSubscriber<T>: FlowableSubscriber<T> {

    override fun onComplete() {

    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {

    }
}