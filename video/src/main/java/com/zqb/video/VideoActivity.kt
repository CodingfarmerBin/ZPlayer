package com.zqb.video

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.activity_video.*

@Route(path = "/video/VideoActivity")
class VideoActivity : AppCompatActivity() {

    private var mPlayer:ZPlayer?=null
    private var mUrl:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager
            .LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_video)
        mUrl = intent.getStringExtra("url")
        mUrl="http://pl30.live.panda.tv/live_panda/fbda9a9b55c6f61920427e674f6ca08e_mid.flv?sign=2a69cd674226c00bbaea1592f08ced1a&time=&ts=5c7bf949&rid=-85478829"
        initPlayer()
    }

    private fun initPlayer() {
        mPlayer = ZPlayer()
        mPlayer!!.setSurfaceView(surfaceView)
        mPlayer!!.setOnPrepareListener(object: ZPlayer.OnPrepareListener {
            override fun onPrepare() {
                runOnUiThread {
                    Toast.makeText(this@VideoActivity, "开始播放", Toast.LENGTH_SHORT).show()
                }
                mPlayer!!.start()
            }
        })
        mPlayer!!.setDataSource(mUrl!!)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                    .LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContentView(R.layout.activity_video)
        mPlayer!!.setSurfaceView(surfaceView)
    }

    override fun onResume() {
        super.onResume()
        mPlayer!!.prepare()
    }

    override fun onStop() {
        super.onStop()
        mPlayer!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer!!.release()
    }
}
