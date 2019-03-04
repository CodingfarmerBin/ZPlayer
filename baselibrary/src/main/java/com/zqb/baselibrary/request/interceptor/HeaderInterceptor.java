package com.zqb.baselibrary.request.interceptor;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Allen on 2017/5/3.
 * <p>
 *
 * @author Allen
 *         请求拦截器  统一添加请求头使用
 */

public class HeaderInterceptor implements Interceptor {

    private Map<String, Object> headerMaps = new TreeMap<>();

    public HeaderInterceptor(Map<String, Object> headerMaps) {
        this.headerMaps = headerMaps;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        if (headerMaps != null && headerMaps.size() > 0) {
            request.addHeader("Content-Type", "text/html; charset=gb2312");
            request.addHeader("Content-Type", "text/html; charset=UTF-8");
            for (Map.Entry<String, Object> entry : headerMaps.entrySet()) {
                Log.d("haha",entry.getKey()+"--"+ String.valueOf(entry.getValue()));
                request.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return chain.proceed(request.build());
    }

}
