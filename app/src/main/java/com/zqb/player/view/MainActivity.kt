package com.zqb.player.view

import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.zqb.baselibrary.http.subscriber.CommonObserver
import com.zqb.baselibrary.mvp.view.BaseActivity
import com.zqb.player.BuildConfig
import com.zqb.player.R
import com.zqb.player.model.MainModel
import com.zqb.player.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainActivity, MainPresenter<MainActivity>,MainModel>(){
    override fun setMainLayout(): Int {
        return R.layout.activity_main
    }

    override fun createPresenter(): MainPresenter<MainActivity> {
        return MainPresenter()
    }

    override fun init() {
        setSupportActionBar(toolbar)
        p!!.request(1)
        fab.setOnClickListener { view ->
            if (!BuildConfig.isComponent) {
                ARouter.getInstance().build("/video/VideoActivity")
                    .withString("title", "shoot")
                    .navigation()
            } else {
                Snackbar.make(view, "当前为组件模式", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            }
        }
    }

    override fun success(type: Int, data: String) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
