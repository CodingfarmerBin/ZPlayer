package com.zqb.player.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.alibaba.android.arouter.launcher.ARouter
import com.zqb.baselibrary.http.HttpUtils
import com.zqb.baselibrary.http.base.Api
import com.zqb.baselibrary.http.base.ApiService
import com.zqb.baselibrary.http.base.BaseBean
import com.zqb.baselibrary.http.intercepter.Transformer
import com.zqb.baselibrary.http.subscriber.CommonSubscriber
import com.zqb.player.BuildConfig
import com.zqb.player.R

import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscription

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (!BuildConfig.isComponent) {
                ARouter.getInstance().build("/video/VideoActivity")
                    .withString("title", "shoot")
                    .navigation()

            } else {
                Snackbar.make(view, "当前为组件模式", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                HttpUtils
                    .post("login", HashMap(),DataBean::class.java)
                    .compose(Transformer().configSchedulers())
                    .compose(Transformer().handleResult())
                    .subscribe(object: CommonSubscriber<DataBean?>() {
                        override fun doOnError(code: Int, msg: String?) {
                            Log.d("haha",msg)
                        }

                        override fun onSubscribe(s: Subscription) {

                        }

                        override fun onNext(t: DataBean?) {

                        }
                    })
//                HttpUtils.createApi(ApiService::class.java)
//                    .post2("login",Api.getRequestBody(HashMap()))
//                    .compose(Transformer().configSchedulers())
//                    .compose(Transformer().handleResult())
//                    .subscribe(object: CommonSubscriber<BaseBean?>() {
//                        override fun doOnError(code: Int, msg: String?) {
//                            Log.d("haha",msg)
//                        }
//
//                        override fun onSubscribe(s: Subscription) {
//
//                        }
//
//                        override fun onNext(t: BaseBean?) {
//
//                        }
//                    })
            }
        }


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
