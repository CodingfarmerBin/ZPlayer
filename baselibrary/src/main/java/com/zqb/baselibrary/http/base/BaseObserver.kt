package com.zqb.baselibrary.http.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class BaseObserver<T>:Observer<T> {

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {

    }
}