package com.zqb.baselibrary.crash

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zqb.baselibrary.base.BaseApplication
import com.zqb.baselibrary.base.Constants
import com.zqb.baselibrary.http.HttpUtils

/**
 * 程序 bug 统一处理
 */
class CrashHandler: Thread.UncaughtExceptionHandler {

    override fun uncaughtException(arg0: Thread, arg1: Throwable) {
        // 在此可以把用户手机的一些信息以及异常信息捕获并上传,由于UMeng在这方面有很程序的api接口来调用，故没有考虑
        Toast.makeText(BaseApplication.getInstance().applicationContext, "程序出现了BUG", Toast.LENGTH_LONG).show()
        Log.e(Constants.REQUEST_TAG,arg1.toString())
        //干掉当前的程序
//        android.os.Process.killProcess(android.os.Process.myPid())
    }

    companion object {
        //双重校验锁式 单例
        val instance: CrashHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CrashHandler()
        }
    }

}
