package com.zqb.baselibrary.http.intercepter

import android.util.Log
import com.zqb.baselibrary.base.Constants
import com.zqb.baselibrary.http.Utils.JsonUtil
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : HttpLoggingInterceptor.Logger {
    private val mMessage = StringBuffer()

    override fun log(message: String) {
        var copyMessage=message
        // 请求或者响应开始
        if (copyMessage.startsWith("--> POST")) {
            mMessage.setLength(0)
            mMessage.append(" ")
            mMessage.append("\r\n")
        }
        if (copyMessage.startsWith("--> GET")) {
            mMessage.setLength(0)
            mMessage.append(" ")
            mMessage.append("\r\n")
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if (copyMessage.startsWith("{") && copyMessage.endsWith("}") || copyMessage.startsWith("[") && copyMessage.endsWith("]")) {
            copyMessage = JsonUtil.formatJson(copyMessage)
        }
        mMessage.append(copyMessage + "\n")
        // 请求或者响应结束，打印整条日志
        if (copyMessage.startsWith("<-- END HTTP")) {
            val s = mMessage.toString()
            Log.e(Constants.REQUEST_TAG,s)
        }
    }
}