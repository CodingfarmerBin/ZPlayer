package com.zqb.baselibrary.http.exception

/**
 * Created by zqb on 2019/3/17.
 *
 * 自定义错误异常
 **/
class ZThrowable : Throwable(){
    var code:Int =0
    var msg:String ?=null

}