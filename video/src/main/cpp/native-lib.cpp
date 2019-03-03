#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include "ZFFmpeg.h"

ZFFmpeg *ffmpeg = 0;
ANativeWindow *window = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;//静态初始化
JavaCallHelper *helper;


JavaVM *javaVm = 0;

int JNI_OnLoad(JavaVM *vm, void *r) {
    javaVm = vm;
    return JNI_VERSION_1_6;
}

//绘制
void renderFrameCallback(uint8_t *data, int lineSize, int w, int h) {
    pthread_mutex_lock(&mutex);
    if (!window) {
        pthread_mutex_unlock(&mutex);
        return;
    }
    //设置窗口属性
    ANativeWindow_setBuffersGeometry(window, w, h, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer window_buffer;
    //锁定窗口的下一个绘图表面用于书写。
    if (ANativeWindow_lock(window, &window_buffer, 0)) {
        ANativeWindow_release(window);
        window = 0;
        pthread_mutex_unlock(&mutex);
        return;
    }
    //填充rgb数据给dst_data
    uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
    //stride:一行有多少数据 RGBA *4
    int dst_linesize = window_buffer.stride * 4;
    //一行一行拷贝
    for (int i = 0; i < window_buffer.height; ++i) {
        //指针加法
        memcpy(dst_data + i * lineSize, data + i * lineSize, dst_linesize);
    }
    //解锁窗口的绘图表面之前锁定后，将新缓冲区发布到显示器。
    ANativeWindow_unlockAndPost(window);
    pthread_mutex_unlock(&mutex);
}

extern "C" JNIEXPORT void JNICALL
Java_com_zqb_video_ZPlayer_nativePrepare(JNIEnv* env, jobject /* this */instance, jstring dataSource_){
    const char *dataSource = env->GetStringUTFChars(dataSource_, 0);
    helper = new JavaCallHelper(javaVm, env, instance);
    ffmpeg = new ZFFmpeg(helper, dataSource);
    ffmpeg->setRenderFrameCallback(renderFrameCallback);
    ffmpeg->prepare();
    env->ReleaseStringUTFChars(dataSource_, dataSource);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_zqb_video_ZPlayer_nativeStart(JNIEnv *env, jobject instance) {
    if (ffmpeg) {
        ffmpeg->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zqb_video_ZPlayer_nativeSetSurface(JNIEnv *env, jobject instance, jobject surface) {
    //防止renderFrameCallback使用时 window被释放（不在同一个线程）
    pthread_mutex_lock(&mutex);
    if (window) {
        //如果已有，把老的释放（横竖屏切换）
        ANativeWindow_release(window);
        window = 0;
    }
    window = ANativeWindow_fromSurface(env, surface);
    pthread_mutex_unlock(&mutex);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zqb_video_ZPlayer_nativeStop(JNIEnv *env, jobject instance) {
    if (ffmpeg) {
        ffmpeg->stop();
    }
    DELETE(helper);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zqb_video_ZPlayer_nativeRelease(JNIEnv *env, jobject instance) {

    pthread_mutex_lock(&mutex);
    if (window) {
        //释放window
        ANativeWindow_release(window);
        window = 0;
    }
    pthread_mutex_unlock(&mutex);
}


