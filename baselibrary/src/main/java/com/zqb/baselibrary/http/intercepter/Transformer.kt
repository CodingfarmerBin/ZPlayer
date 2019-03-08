package com.zqb.baselibrary.http.intercepter

import android.app.Dialog
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 */
class Transformer {
    /**
     * 无参数
     *
     * @param <T> 泛型
     * @return 返回Observable
    </T> */
    fun <T> configSchedulers(): FlowableTransformer<T, T> {
        return configSchedulers(null)
    }

    /**
     * 带参数  显示loading对话框
     *
     * @param dialog loading
     * @param <T>    泛型
     * @return 返回Observable
    </T> */
    fun <T> configSchedulers(dialog: Dialog?): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe {
                    dialog?.show()
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    dialog?.dismiss()
                }
        }
    }
}