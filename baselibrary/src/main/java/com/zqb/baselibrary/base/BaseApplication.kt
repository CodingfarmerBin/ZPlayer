package com.zqb.baselibrary.base

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.zqb.baselibrary.crash.CrashHandler
import com.zqb.baselibrary.http.HttpUtils
import com.zqb.baselibrary.http.config.OkHttpConfig
import com.zqb.baselibrary.http.cookie.store.SPCookieStore
import java.util.HashMap

/**
 * Created by zqb on 2019/3/2.
 **/
open class BaseApplication : Application() {

    companion object {
        private var instance: BaseApplication? = null

        fun getInstance(): BaseApplication {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance=this
        initARouter()
        initRequest()
        initCrashHandler()
    }

    private fun initCrashHandler() {
        if(!Constants.isDebug) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler.instance)
        }
    }

    private val headerMaps = HashMap<String, Any>()

    private fun initRequest() {
        OkHttpConfig.Builder()
            //全局的请求头信息
            .setHeaders(headerMaps)
            //开启缓存策略(默认false)
            //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
            //2、在没有网络的时候，去读缓存中的数据。
            .setCache(true)
            //全局持久话cookie,保存到内存（new MemoryCookieStore()）或者保存到本地（new SPCookieStore(this)）
            //不设置的话，默认不对cookie做处理
            .setCookieType(SPCookieStore(this))
            //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
            //.setAddInterceptor(null)
            //全局ssl证书认证
            //1、信任所有证书,不安全有风险（默认信任所有证书）
            .setSslSocketFactory()
            //2、使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(cerInputStream)
            //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
            //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
            //全局超时配置
            .setReadTimeout(20)
            //全局超时配置
            .setWriteTimeout(20)
            //全局超时配置
            .setConnectTimeout(20)
            //全局是否打开请求log日志
            .setDebug(true)
            .build()
    }

    private fun initARouter() {
        if(Constants.isDebug) {
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }
}