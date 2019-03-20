package com.zqb.baselibrary.http.base

/**
 *  GSON 返回数据bean 基类
 *
 *  处理返回错误码 和错误描述（根据后端而定）
 */
open class BaseBean {
    /**
     * 错误码
     */
    var code: Int = 0
    /**
     * 错误描述
     */
    var message: String? = null

}