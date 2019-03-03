//
// Created by 张清斌 on 2018/10/7.
//

#ifndef ZPLAYER_BASECHANNEL_H
#define ZPLAYER_BASECHANNEL_H

#include "safe_queue.h"
#include "macro.h"
extern "C"{
#include <libavcodec/avcodec.h>
};

class BaseChannel{
public:
    BaseChannel(int id,AVCodecContext* context,AVRational time_base):id(id),avCodecContext(context),time_base(time_base){
        frames.setReleaseCallback(releaseAVFrame);
        packets.setReleaseCallback(releaseAVPacket);
    }
    //虚方法 让子类能够调用父类虚构方法
    virtual ~BaseChannel(){
        packets.clear();
        frames.clear();
        if(avCodecContext){
            avcodec_close(avCodecContext);
            avcodec_free_context(&avCodecContext);
            avCodecContext=0;
        }
    }
    //释放AVPacket
    static void releaseAVPacket(AVPacket*& packet){
        if(packet) {
            av_packet_free(&packet);
            packet=0;
        }
    }
    //释放AVFrame
    static void releaseAVFrame(AVFrame*& frame){
        if(frame) {
            av_frame_free(&frame);
            frame=0;
        }
    }
    //纯虚方法 相当于抽象方法
    virtual void play()=0;
    virtual void stop()=0;
    int id;
    AVCodecContext* avCodecContext;
    //编码数据包队列
    SafeQueue<AVPacket*> packets;
    //解码数据包队列
    SafeQueue<AVFrame*> frames;
    bool isPlaying = 0;

    AVRational time_base;
};
#endif //ZPLAYER_BASECHANNEL_H
