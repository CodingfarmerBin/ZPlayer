package com.zqb.player.model

import com.zqb.baselibrary.http.HttpUtils
import com.zqb.baselibrary.http.base.Api
import com.zqb.baselibrary.http.base.ApiService
import com.zqb.baselibrary.http.intercepter.Transformer
import com.zqb.baselibrary.mvp.model.BaseModel
import com.zqb.player.view.NewsBean
import io.reactivex.Observable

/**
 * Created by zqb on 2019/3/22
 *
 */
class MainModel :BaseModel<MainModel>(){

    fun getMainData(map:HashMap<String,Any>): Observable<NewsBean> {
        return HttpUtils
            .post("getJoke?page=1&count=2&type=video",map, NewsBean::class.java)
    }

    fun getMainData2(map:HashMap<String,Any>): Observable<String> {
        return HttpUtils.createApi(ApiService::class.java)
            .post2("getJoke?page=1&count=2&type=video", Api.getRequestBody(map))
            .compose(Transformer().configSchedulers())
    }
}