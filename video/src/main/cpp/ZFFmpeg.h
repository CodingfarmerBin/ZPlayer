//
// Created by 张清斌 on 2018/10/6.
//

#ifndef ZPLAYER_ZFFMPEG_H
#define ZPLAYER_ZFFMPEG_H

#include "JavaCallHelper.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

extern "C" {
#include <libavformat/avformat.h>
}

class ZFFmpeg {
public:
    ZFFmpeg(JavaCallHelper *callHelper, const char *dataSource);

    ~ZFFmpeg();

    void prepare();

    void _prepare();

    void start();

    void _start();

    void setRenderFrameCallback(RenderFrameCallback callback);

    void stop();

private:
    char *dataSource;
    JavaCallHelper *callHelper;
    RenderFrameCallback callback;
public:
    bool isPlaying = 0;
    pthread_t pid_prepare;
    pthread_t pid_play;
    pthread_t pid_stop;
    AudioChannel *audioChannel = 0;
    VideoChannel *videoChannel = 0;
    AVFormatContext *formatContext = 0;
};

#endif //ZPLAYER_ZFFMPEG_H
