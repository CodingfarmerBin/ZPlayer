package com.zqb.baselibrary.http.base

import io.reactivex.FlowableSubscriber
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class BaseObserver<T>: Observer<T> {

    abstract fun doOnError(code: Int, msg: String?)

    abstract fun doOnSubscribe(s: Disposable)
}