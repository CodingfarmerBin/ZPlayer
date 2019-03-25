package com.zqb.baselibrary.mvp.contact

import android.app.Dialog

/**
 * Created by zqb on 2019/3/22
 *
 */
interface IView {

    fun success(type:Int,data:String)
    fun getLoadingView(): Dialog
    fun showToast(msg:String?)
}