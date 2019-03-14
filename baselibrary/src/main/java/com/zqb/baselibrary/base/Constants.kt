package com.zqb.baselibrary.base

import android.os.Environment

object Constants {
    //是否是测试模式
    const val isDebug = true
    //网络请求缓存最大值
    const val httpCacheSize = (1024 * 1024 * 100).toLong()
    //网络请求超时时间
    const val httpTimeOut = 10.toLong()
    //网络请求缓存地址
    val httpCatchPath = "${BaseApplication().externalCacheDir}/ZCatch"
    //网络请求 统一Log管理
    const val REQUEST_TAG="REQUEST"
    //网络请求 基地址
    const val BASE_URL="http://192.168.0.28:8080/api/"
}