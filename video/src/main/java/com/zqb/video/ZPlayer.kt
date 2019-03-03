package com.zqb.video

import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by zqb on 2018/10/6.
 */
class ZPlayer : SurfaceHolder.Callback {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    //播放的文件或者地址
    private var dataSource: String? = null
    private var mHolder: SurfaceHolder? = null
    private var mOnPrepareListener: OnPrepareListener? = null

    fun setDataSource(dataSource: String) {
        this.dataSource = dataSource
    }

    fun start() {
        nativeStart()
    }

    fun stop() {
        nativeStop()
    }

    fun release() {
        mHolder!!.removeCallback(this)
        nativeRelease()
    }

    /**
     * 设置播放显示的画布
     */
    fun setSurfaceView(surfaceView: SurfaceView) {
        if (mHolder != null) {
            mHolder!!.removeCallback(this)
        }
        mHolder = surfaceView.holder
        mHolder!!.addCallback(this)
    }

    /**
     * 准备好要播放的视频
     */
    fun prepare() {
        nativePrepare(dataSource)
    }

    fun onError(errorCode: Int) {
        Log.d("haha", "$errorCode!@")
    }

    fun onPrepare() {
        if (mOnPrepareListener != null) {
            mOnPrepareListener!!.onPrepare()
        }
    }

    fun setOnPrepareListener(onPrepareListener: OnPrepareListener) {
        mOnPrepareListener = onPrepareListener
    }

    interface OnPrepareListener {
        fun onPrepare()
    }

    /**
     * 画布创建
     */
    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    /**
     * 画布发生了变化（横竖屏切换等）
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeSetSurface(holder.surface)
    }

    /**
     * 销毁画布（画布不可见）
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        nativeStop()
    }

    private external fun nativePrepare(dataSource: String?)
    private external fun nativeStart()
    private external fun nativeStop()
    private external fun nativeRelease()
    private external fun nativeSetSurface(surface: Surface)
}
