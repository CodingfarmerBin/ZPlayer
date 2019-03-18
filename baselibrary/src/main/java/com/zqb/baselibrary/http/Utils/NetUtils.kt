package com.zqb.baselibrary.http.Utils

import android.content.Context
import android.net.ConnectivityManager
import com.zqb.baselibrary.base.BaseApplication

/**
 * <pre>
 * @author : Allen
 * date    : 2018/06/14
 * desc    : 管理管理类
 * version : 1.0
</pre> *
 */
object NetUtils {
    /**
     * 判断是否有网络
     *
     * @return 返回值
     */
    val isNetworkConnected: Boolean
        get() {
                val mConnectivityManager =
                    BaseApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            return false
        }
}
