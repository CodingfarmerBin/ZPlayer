package com.zqb.baselibrary.http.base

import io.reactivex.FlowableSubscriber
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class BaseSubscriber<T>: FlowableSubscriber<T> {

    abstract fun doOnError(code: Int, msg: String?)
}