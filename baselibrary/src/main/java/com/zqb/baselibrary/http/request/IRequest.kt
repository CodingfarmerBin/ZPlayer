package com.zqb.baselibrary.http.request

import okhttp3.RequestBody

abstract class IRequest{
    var url:String?=null
    var requestBody:RequestBody?=null

    abstract fun setUrl(url:String):IRequest
}
