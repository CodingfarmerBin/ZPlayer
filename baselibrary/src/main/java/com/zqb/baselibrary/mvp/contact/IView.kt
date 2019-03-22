package com.zqb.baselibrary.mvp.contact

/**
 * Created by zqb on 2019/3/22
 *
 */
interface IView {

    fun success(type:Int,data:String)
    fun getLoadingView(): IDialog
    fun showToast(msg:String)
}