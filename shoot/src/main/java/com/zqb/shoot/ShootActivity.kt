package com.zqb.shoot

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/shoot/ShootActivity")
class ShootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoot)
        val title = intent.getStringExtra("title")
        Log.d("haha","-->$title")
    }
}
