package com.zqb.baselibrary.http.subscriber

import com.zqb.baselibrary.http.base.ApiException
import com.zqb.baselibrary.http.base.BaseSubscriber
import com.zqb.baselibrary.http.exception.ZThrowable

/**
 * Created by zqb on 2019/3/9.
 **/
abstract class CommonSubscriber<T> : BaseSubscriber<T>() {
    override fun onComplete() {

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