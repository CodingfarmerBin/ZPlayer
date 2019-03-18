package com.zqb.baselibrary.http.base

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
 * 错误码转换
 **/
class ApiException(throwable: Throwable, var code: Int) : Exception(throwable) {
     var msg: String? = null

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

        fun handleException(e: Throwable): ApiException {
            val ex: ApiException
            if (e is HttpException) {
                ex = ApiException(e, e.code())
                ex.msg = e.message
            } else if (e is SocketTimeoutException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.msg = "网络连接超时，请检查您的网络状态，稍后重试！"
            } else if (e is ConnectException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.msg = "网络连接异常，请检查您的网络状态，稍后重试！"
            } else if (e is ConnectTimeoutException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.msg = "网络连接超时，请检查您的网络状态，稍后重试！"
            } else if (e is UnknownHostException) {
                ex = ApiException(e, ERROR.TIMEOUT_ERROR)
                ex.msg = "网络连接异常，请检查您的网络状态，稍后重试！"
            } else if (e is NullPointerException) {
                ex = ApiException(e, ERROR.NULL_POINTER_EXCEPTION)
                ex.msg = "空指针异常"
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = ApiException(e, ERROR.SSL_ERROR)
                ex.msg = "证书验证失败"
            } else if (e is ClassCastException) {
                ex = ApiException(e, ERROR.CAST_ERROR)
                ex.msg = "类型转换错误"
            } else if (e is JsonParseException
                || e is JSONException
                || e is JsonSerializer<*>
                || e is NotSerializableException
                || e is ParseException
            ) {
                ex = ApiException(e, ERROR.PARSE_ERROR)
                ex.msg = "解析错误"
            } else if (e is IllegalStateException) {
                ex = ApiException(e, ERROR.ILLEGAL_STATE_ERROR)
                ex.msg = e.message
            } else {
                ex = ApiException(e, ERROR.UNKNOWN)
            }
            return ex
        }
    }
}