package com.zqb.baselibrary.http.exception

import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * Created by zqb on 2019/3/17.
 *
 * 自定义错误异常
 **/
class ZThrowable(var code: Int,var msg:String) : Throwable(){

    var e:Throwable ?=null

    fun setCode(code:Int):ZThrowable{
        this.code=code
        return this
    }

    fun setMsg(msg:String):ZThrowable{
        this.msg=msg
        return this
    }

    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 1000
        /**
         * 连接超时
         */
        val TIMEOUT_ERROR = 1001
        /**
         * 空指针错误
         */
        val NULL_POINTER_EXCEPTION = 1002

        /**
         * 证书出错
         */
        val SSL_ERROR = 1003

        /**
         * 类转换错误
         */
        val CAST_ERROR = 1004

        /**
         * 解析错误
         */
        val PARSE_ERROR = 1005

        /**
         * 非法数据异常
         */
        val ILLEGAL_STATE_ERROR = 1006

    }

    companion object {

        fun handleException(e: Throwable): ZThrowable {
            val ex: ZThrowable
            if (e is HttpException) {
                ex = ZThrowable(e.code(), e.message())
                ex.e=e
            } else if (e is SocketTimeoutException) {
                ex = ZThrowable(ERROR.TIMEOUT_ERROR, "网络连接超时，请检查您的网络状态，稍后重试！")
                ex.e=e
            } else if (e is ConnectException) {
                ex = ZThrowable(ERROR.TIMEOUT_ERROR, "网络连接异常，请检查您的网络状态，稍后重试！")
                ex.e=e
            } else if (e is ConnectTimeoutException) {
                ex = ZThrowable(ERROR.TIMEOUT_ERROR, "网络连接超时，请检查您的网络状态，稍后重试！")
                ex.e=e
            } else if (e is UnknownHostException) {
                ex = ZThrowable(ERROR.TIMEOUT_ERROR, "网络连接异常，请检查您的网络状态，稍后重试！")
                ex.e=e
            } else if (e is NullPointerException) {
                ex = ZThrowable(ERROR.NULL_POINTER_EXCEPTION, "空指针异常")
                ex.e=e
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = ZThrowable(ERROR.SSL_ERROR, "证书验证失败")
                ex.e=e
            } else if (e is ClassCastException) {
                ex = ZThrowable(ERROR.CAST_ERROR, "类型转换错误")
                ex.e=e
            } else if (e is JsonParseException
                || e is JSONException
                || e is JsonSerializer<*>
                || e is NotSerializableException
                || e is ParseException
            ) {
                ex = ZThrowable(ERROR.PARSE_ERROR, "解析错误")
                ex.e=e
            } else if (e is IllegalStateException) {
                ex = ZThrowable(ERROR.ILLEGAL_STATE_ERROR, e.message!!)
                ex.e=e
            } else {
                ex = ZThrowable(ERROR.UNKNOWN, "未知错误")
                ex.e=e
            }
            return ex
        }
    }
}