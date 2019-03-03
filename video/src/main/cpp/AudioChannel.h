//
// Created by 张清斌 on 2018/10/6.
//

#ifndef ZPLAYER_AUDIOCHANNEL_H
#define ZPLAYER_AUDIOCHANNEL_H


#include "BaseChannel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
extern "C"{
#include <libswresample/swresample.h>
};

class AudioChannel: public BaseChannel {
public:
    AudioChannel(int id, AVCodecContext *context,AVRational time_base);
    ~AudioChannel();

    void play();

    void decode();

    void _play();

    void stop();

    int getPcm();
private:
    pthread_t pid_audio_decode;
    pthread_t pid_audio_play;
    /**
     * OpenSL ES
     * 声明指针 需初始化否则会有一个野指针
     */
    //引擎与引擎接口
    SLObjectItf  engineObject=0;
    SLEngineItf  engineInterface=0;
    //混音器
    SLObjectItf outputMixObject=0;
    //播放器
    SLObjectItf bqPlayerObject=0;
    //播放器接口
    SLPlayItf bqPlayerInterface=0;

    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueueInterface=0;
    //重采样
    SwrContext* swrContext=0;

public:
    uint8_t *data=0;

    int out_channels;
    int out_sampleSize;
    int out_sample_rate;
    double clock;
};


#endif //ZPLAYER_AUDIOCHANNEL_H
