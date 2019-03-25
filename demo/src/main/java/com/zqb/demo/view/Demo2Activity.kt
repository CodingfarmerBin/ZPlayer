package com.zqb.demo.view

import com.zqb.baselibrary.http.subscriber.CommonObserver
import com.zqb.baselibrary.mvp.view.BaseSimpleActivity
import com.zqb.demo.R
import com.zqb.demo.bean.DemoBean
import com.zqb.demo.presenter.Demo2Presenter
import io.reactivex.disposables.Disposable

import kotlinx.android.synthetic.main.activity_demo2.*

class Demo2Activity : BaseSimpleActivity<Demo2Activity,Demo2Presenter<Demo2Activity>>() {
    override fun setMainLayout(): Int {
        return R.layout.activity_demo2
    }

    override fun createPresenter(): Demo2Presenter<Demo2Activity> {
        return Demo2Presenter()
    }

    override fun init() {
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
          p!!.request()
              .subscribe(object : CommonObserver<List<DemoBean.ResultBean>>() {
                  override fun doOnSubscribe(s: Disposable) {
                      p!!.add(s) //处理 结束请求 结束事件流
                  }

                  override fun onNext(t: List<DemoBean.ResultBean>) {
                      showToast(t.toString())
                  }

                  override fun doOnError(code: Int, msg: String?) {
                      showToast(msg)
                  }
              })
        }
    }

    override fun success(type: Int, data: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
