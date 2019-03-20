package com.zqb.baselibrary.http.intercepter

import android.app.Dialog
import android.util.Log
import com.zqb.baselibrary.http.exception.ZThrowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.zqb.baselibrary.http.base.BaseBean
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.json.JSONObject


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

    /**
     * 配置所有，线程切换 dialog 和结果处理
     *
     * @param dialog loading
     * @param <T>    泛型
     * @return 返回Observable
     */
    fun <T> configAll(dialog: Dialog?): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe {
                    dialog?.show()
                }
                .flatMap {
                    if(it is BaseBean){
                        if(it.code!=200) {
                            val throwable = ZThrowable()
                            throwable.code=it.code
                            throwable.msg=it.message
                            Flowable.error<T>(throwable)
                        }
                    }else if(it is String){
                        val jsonObject = JSONObject(it)
                        val msg = jsonObject.optString("msg")
                        val code = jsonObject.optInt("code")
                        if(code!=200) {
                            val throwable = ZThrowable()
                            throwable.code=code
                            throwable.msg=msg
                            Flowable.error<T>(throwable)
                        }
                    }
                    createData(it)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    dialog?.dismiss()
                }
        }
    }

    /**
     * 处理网络请求的结果
     */
    fun <T> handleResult(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream.flatMap {
                if(it is BaseBean){
                    if(it.code!=200) {
                        val throwable = ZThrowable()
                        throwable.code=it.code
                        throwable.msg=it.message
                        Flowable.error<T>(throwable)
                    }
                }else if(it is String){
                    val jsonObject = JSONObject(it)
                    val msg = jsonObject.optString("msg")
                    val code = jsonObject.optInt("code")
                    if(code!=200) {
                        val throwable = ZThrowable()
                        throwable.code=code
                        throwable.msg=msg
                        Flowable.error<T>(throwable)
                    }
                }
                createData(it)
            }
        }
    }

    private fun <T> createData(t: T): Flowable<T> {
        return Flowable.create({ emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }, BackpressureStrategy.BUFFER)
    }


}