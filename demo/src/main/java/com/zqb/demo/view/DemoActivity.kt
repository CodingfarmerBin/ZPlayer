package com.zqb.demo.view

import android.content.Intent
import com.zqb.baselibrary.http.subscriber.CommonObserver
import com.zqb.baselibrary.mvp.view.BaseActivity
import com.zqb.demo.R
import com.zqb.demo.bean.DemoBean
import com.zqb.demo.model.MainModel
import com.zqb.demo.presenter.MainPresenter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : BaseActivity<DemoActivity, MainPresenter<DemoActivity>, MainModel>(){
    override fun setMainLayout(): Int {
        return R.layout.activity_demo
    }

    override fun createPresenter(): MainPresenter<DemoActivity> {
        return MainPresenter()
    }

    override fun init() {
        request.setOnClickListener {
            p!!.request(0)
//            p!!.request()
//                .subscribe(object : CommonObserver<List<DemoBean.ResultBean>>() {
//                    override fun doOnSubscribe(s: Disposable) {
//                        p!!.add(s) //处理 结束请求 结束事件流
//                    }
//
//                    override fun onNext(t: List<DemoBean.ResultBean>) {
//                        showToast(t.toString())
//                    }
//
//                    override fun doOnError(code: Int, msg: String?) {
//                        showToast(msg)
//                    }
//                })
        }


    }

    override fun success(type: Int, data: String) {
        p!!.bean
        startActivity(Intent(this,Demo2Activity::class.java))
    }
}
