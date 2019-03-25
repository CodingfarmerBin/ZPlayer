package com.zqb.player.presenter

import com.zqb.baselibrary.http.exception.ZThrowable
import com.zqb.baselibrary.http.intercepter.Transformer
import com.zqb.baselibrary.http.subscriber.CommonObserver
import com.zqb.baselibrary.mvp.contact.IView
import com.zqb.baselibrary.mvp.presenter.BasePresenter
import com.zqb.player.model.MainModel
import com.zqb.player.view.NewsBean
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

/**
 * Created by zqb on 2019/3/22
 *
 */
class MainPresenter<T> :BasePresenter<T,MainModel>() {
    override fun createModel(): MainModel {
        return MainModel()
    }

    fun request():Observable<List<NewsBean.ResultBean>>{
        val view = getView() as IView
        return getModel()
            .getMainData(HashMap())
            .compose(Transformer().configAll(view.getLoadingView()))
            .compose(Transformer().handleResult())
            .flatMap(object: Function<NewsBean, Observable<List<NewsBean.ResultBean>>> {
                override fun apply(t: NewsBean): Observable<List<NewsBean.ResultBean>> {
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

    fun request(type :Int){//两种请求方式
        val view = getView() as IView
        if(type==0) {
            getModel()
                .getMainData(HashMap())
                .compose(Transformer().configAll(view.getLoadingView()))
                .subscribe(object : CommonObserver<NewsBean?>() {
                    override fun doOnSubscribe(s: Disposable) {
                        add(s) //处理 结束请求 结束事件流
                    }

                    override fun onNext(t: NewsBean) {
                        view.showToast(t.toString())
                    }

                    override fun doOnError(code: Int, msg: String?) {
                        view.showToast(msg)
                    }

                })
        }else{
            getModel()
                .getMainData2(HashMap())
                .subscribe(object : CommonObserver<String>() {
                    override fun doOnSubscribe(s: Disposable) {
                        add(s) //处理 结束请求 结束事件流
                    }

                    override fun onNext(t: String) {
                        view.showToast(t)
                    }

                    override fun doOnError(code: Int, msg: String?) {
                        view.showToast(msg)
                    }

                })
        }
    }
}