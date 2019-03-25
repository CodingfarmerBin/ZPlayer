package com.zqb.demo.presenter

import com.zqb.baselibrary.http.exception.ZThrowable
import com.zqb.baselibrary.http.intercepter.Transformer
import com.zqb.baselibrary.http.subscriber.CommonObserver
import com.zqb.baselibrary.mvp.contact.IView
import com.zqb.baselibrary.mvp.presenter.BasePresenter
import com.zqb.demo.bean.DemoBean
import com.zqb.demo.model.MainModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

/**
 * Created by zqb on 2019/3/22
 *
 */
class MainPresenter<T> :BasePresenter<T,MainModel>() {

//    var bean:DemoBean?=null

    override fun createModel(): MainModel {
        return MainModel()
    }

    fun request():Observable<List<DemoBean.ResultBean>>{
        val view = getView() as IView
        return getModel()
            .getMainData(HashMap())
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

     lateinit var bean: DemoBean

    fun request(type :Int){//两种请求方式
        val view = getView() as IView
        if(type==0) {
            getModel()
                .getMainData(HashMap())
                .compose(Transformer().configSchedulers(view.getLoadingView()))
                .compose(Transformer().handleResult())
                .subscribe(object : CommonObserver<DemoBean?>() {
                    override fun doOnSubscribe(s: Disposable) {
                        add(s) //处理 结束请求 结束事件流
                    }

                    override fun onNext(t: DemoBean) {
                        view.showToast(t.toString())
                        bean=t
                        view.success(0,"")
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