package com.zqb.baselibrary.mvp.view

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zqb.baselibrary.mvp.contact.IDialog
import com.zqb.baselibrary.mvp.contact.IView
import com.zqb.baselibrary.mvp.presenter.BasePresenter

/**
 * Created by zqb on 2019/3/21
 *
 */
abstract class BaseActivity<V, T : BasePresenter<V,M>,M> : AppCompatActivity() , IView {

    protected var p: T? = null

    var mProgressDialog: IDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setMainLayout())
        p = createPresenter()
        p!!.attachView(this as V)
        init()
    }

    /***
     * 初始化布局
     */
    protected abstract fun setMainLayout(): Int

    /**
     * 创建 Presenter
     */
    abstract fun createPresenter(): T

    /**
     * 初始化
     */
    protected abstract fun init()

    override fun showToast(msg: String) {
        Toast.makeText(applicationContext,msg,Toast.LENGTH_LONG).show()
    }

    protected fun showToast(msg: Int) {
        Toast.makeText(applicationContext,msg,Toast.LENGTH_LONG).show()
    }

    override fun getLoadingView(): IDialog {
        if(mProgressDialog==null){
            mProgressDialog =LoadingDialog(this)
        }
        return mProgressDialog!!
    }

    //设置背景变暗
    fun setBgBlack() {
        val lp = window.attributes
        lp.alpha = 0.6f
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    //设置背景恢复
    fun setBgWhite() {
        val lp = window.attributes
        lp.alpha = 1.0f
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onDestroy() {
        super.onDestroy()
        p!!.detachView()
        if(mProgressDialog!=null){
            mProgressDialog!!.hide()
        }
    }

}