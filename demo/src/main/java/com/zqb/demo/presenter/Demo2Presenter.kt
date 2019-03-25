package com.zqb.demo.presenter

import com.zqb.baselibrary.http.HttpUtils
import com.zqb.baselibrary.http.exception.ZThrowable
import com.zqb.baselibrary.http.intercepter.Transformer
import com.zqb.baselibrary.mvp.contact.IView
import com.zqb.baselibrary.mvp.presenter.BaseSimplePresenter
import com.zqb.demo.bean.DemoBean
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by zqb on 2019/3/25
 */
class Demo2Presenter<T>:BaseSimplePresenter<T>(){

    fun request(): Observable<List<DemoBean.ResultBean>> {
        val view = getView() as IView
        return HttpUtils
            .post("getJoke?page=1&count=2&type=video",HashMap(), DemoBean::class.java)
            .compose(Transformer().configAll(view.getLoadingView()))
            .flatMap(object: Function<DemoBean, Observable<List<DemoBean.ResultBean>>> {
                override fun apply(t: DemoBean): Observable<List<DemoBean.ResultBean>> {
                    return Observable.create{
                        if(t.result==null || t.result!!.isEmpty()) {
                            it.onError(ZThrowable(1000,"").setCode(1000).setMsg("无数据"))
                        }else{
                            it.onNext(t.result!!)
                        }
                    }
                }
            })
    }
}
