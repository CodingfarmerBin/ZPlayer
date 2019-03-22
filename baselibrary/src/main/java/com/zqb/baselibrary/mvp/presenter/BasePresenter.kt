package com.zqb.baselibrary.mvp.presenter

import com.zqb.baselibrary.mvp.contact.IPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Created by zqb on 2019/3/21
 *
 */
abstract class BasePresenter<T,M> : IPresenter<T> {

    private var mDisposables: CompositeDisposable? = null
    private var weakReference: WeakReference<T>? = null
    private var modelReference: WeakReference<M>? = null

    abstract fun createModel():M

    override fun attachView(view: T) {
        weakReference = WeakReference(view)
        modelReference = WeakReference(createModel())
    }

    override fun detachView() {
        unSubscribe()
        if (weakReference != null) {
            weakReference!!.clear()
            weakReference = null
        }

        if (modelReference != null) {
            modelReference!!.clear()
            modelReference = null
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


    fun getView(): T {
        return weakReference?.get()!!
    }

    fun getModel(): M {
        return modelReference?.get()!!
    }
}