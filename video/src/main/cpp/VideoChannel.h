//
// Created by 张清斌 on 2018/10/6.
//

#ifndef ZPLAYER_VIDEOCHANNEL_H
#define ZPLAYER_VIDEOCHANNEL_H


#include "BaseChannel.h"
#include "AudioChannel.h"

extern "C" {
#include <libswscale/swscale.h>
};

typedef void (*RenderFrameCallback)(uint8_t *, int, int, int);

class VideoChannel : public BaseChannel {
public:
    VideoChannel(int id, AVCodecContext *context, AVRational time_base, int fps);

    ~VideoChannel();

    //为了拿到音频的时间戳 ，根据音频同步视频
    void setAudioChannel(AudioChannel *audioChannel);

    void play();

    void stop();

    void decode();

    void render();

    void setRenderFrameCallback(RenderFrameCallback callback);

private:
    pthread_t pid_decode;
    pthread_t pid_render;
    SwsContext *swsContext = 0;

    int fps;
    AudioChannel *audioChannel = 0;
public:
    RenderFrameCallback callback = 0;
};


#endif //ZPLAYER_VIDEOCHANNEL_H
