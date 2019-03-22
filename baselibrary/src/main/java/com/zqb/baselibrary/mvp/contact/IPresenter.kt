package com.zqb.baselibrary.mvp.contact

/**
 * Created by zqb on 2019/3/21
 *
 */
interface IPresenter<V> {

    fun attachView(view: V)
    fun detachView()
}