package com.zqb.baselibrary.mvp.presenter

import android.util.Log
import com.zqb.baselibrary.mvp.contact.IPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Created by zqb on 2019/3/21
 *
 */
abstract class BaseSimplePresenter<T> : IPresenter<T> {

    private var mDisposables: CompositeDisposable? = null
    private var weakReference: WeakReference<T>? = null


    override fun attachView(view: T) {
        weakReference = WeakReference(view)
    }

    override fun detachView() {
        unSubscribe()
        if (weakReference != null) {
            weakReference!!.clear()
            weakReference = null
        }
    }


    private fun unSubscribe() {
        if (mDisposables != null) {
            mDisposables!!.dispose()
        }
    }

    fun add(disposable: Disposable) {
        if (mDisposables == null) {
            mDisposables = CompositeDisposable()
        }
        mDisposables!!.add(disposable)
    }


    fun getView(): T ?{
        return weakReference?.get()
    }

}