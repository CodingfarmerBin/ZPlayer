package com.zqb.baselibrary.http.intercepter

import android.app.Dialog
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber

/**
 * Created by zqb on 2019/3/9.
 *
 * 线程调度
 **/
class Transformer {
    /**
     * 无参数
     *
     * @param <T> 泛型
     * @return 返回Observable
     */
    fun <T> configSchedulers(): FlowableTransformer<T, T> {
        return configSchedulers(null)
    }

    /**
     * 带参数  显示loading对话框
     *
     * @param dialog loading
     * @param <T>    泛型
     * @return 返回Observable
     */
    fun <T> configSchedulers(dialog: Dialog?): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .onBackpressureBuffer()
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

    fun <T> trans(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            upstream -> upstream.onErrorResumeNext({ s: Subscriber<in T>? ->  })
        }
    }
//        return stringObservable -> stringObservable.map(s -> {
//
//            Response parseJson = GsonUtil.parseJson(s, Response.class);
//
//            if (null == parseJson) {
//                throw new RuntimeException("null == parseJson");
//            }
//
//            if (PatternsUtil.isNum(parseJson.data.toString())) {
//                throw new RuntimeException(parseJson.data.toString());
//            }
//
//            return GsonUtil.parseJson(s, UserModel.class);
//        }).onErrorResumeNext(new HttpResponseFunc<>());
    }