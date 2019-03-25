package com.zqb.baselibrary.mvp.view

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import com.zqb.baselibrary.R
import com.zqb.baselibrary.mvp.contact.IDialog

/**
 * Created by zqb on 2019/3/22
 *
 * Loading 图
 */
class LoadingDialog(context: Context) : ProgressDialog(context) ,IDialog{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMessage("正在加载中...")
    }
}
