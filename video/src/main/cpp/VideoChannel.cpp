//
// Created by 张清斌 on 2018/10/6.
//
extern "C" {
#include <libavutil/imgutils.h>
#include <libavutil/time.h>
}

#include "VideoChannel.h"

/**
 * 1，解码
 */
void *decode_task(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->decode();
    return 0;
}

/**
 *  2，渲染
 */
void *render_task(void *args) {
    VideoChannel *videoChannel = static_cast<VideoChannel *>(args);
    videoChannel->render();
    return 0;
}

/**
 * 丢包 直到下一个关键帧
 */
void dropAVPacket(queue<AVPacket *> &q) {
    while (!q.empty()) {
        AVPacket *packet = q.front();
        //如果不属于i帧
        if (packet->flags != AV_PKT_FLAG_KEY) {
            BaseChannel::releaseAVPacket(packet);
            q.pop();
        } else {
            break;
        }
    }
}

/**
 * 丢包
 */
void dropAVFrame(queue<AVFrame *> &q) {
    if (!q.empty()) {
        AVFrame *frame = q.front();
        //如果不属于i帧
        BaseChannel::releaseAVFrame(frame);
        q.pop();
    }
}

VideoChannel::VideoChannel(int id, AVCodecContext *context, AVRational time_base, int fps)
        : BaseChannel(id, context, time_base) {
    this->fps = fps;
//    packets.setSyncHandle(dropAVPacket);
//    packets.sync();
    frames.setSyncHandle(dropAVFrame);
}

VideoChannel::~VideoChannel() {

}

void VideoChannel::setAudioChannel(AudioChannel *audioChannel) {
    this->audioChannel = audioChannel;
}

void VideoChannel::play() {
    isPlaying = 1;
    frames.setWork(1);
    packets.setWork(1);
    //解码线程
    pthread_create(&pid_decode, 0, decode_task, this);
    //渲染线程
    pthread_create(&pid_render, 0, render_task, this);

}


//解码
void VideoChannel::decode() {
    AVPacket *packet = 0;
    //正在播放
    while (isPlaying) {
        int ret = packets.pop(packet);
        //pop时中间有线程等待，取出成功之后重新判断当前是否是播放状态
        if (!isPlaying) {
            break;
        }
        //当此取出来的包是否成功，不成功继续
        if (!ret) {
            continue;
        }
        //把包丢给解码器
        ret = avcodec_send_packet(avCodecContext, packet);
        releaseAVPacket(packet);
        //如果是重试,解码器满了
//        if(ret ==AVERROR(EAGAIN)){
//            continue;
//        }else
        if (ret != 0) {
            break;
        }
        //AVFrame代表了一个图像(将这个图像先显示出来)
        AVFrame *avFrame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, avFrame);
        //需要更多的数据才能进行界面
        if (ret == AVERROR(EAGAIN)) {
            continue;
        } else if (ret != 0) {
            break;
        }
        //再开一个线程来播放（流畅度，方便音视频同步）
        frames.push(avFrame);
    }
    releaseAVPacket(packet);
}

//播放
void VideoChannel::render() {
    //目标：RGBA
    //参数：AVPixelFormat原图像格式（yuv,rgb）
    //SWS_BILINEAR:现在要使用的转换算法（比较快，质量比较好等）
    swsContext = sws_getContext(avCodecContext->width, avCodecContext->height,
                                avCodecContext->pix_fmt, avCodecContext->width,
                                avCodecContext->height, AV_PIX_FMT_RGBA, SWS_BILINEAR,
                                0, 0, 0);
    //每个画面刷新的间隔 单位是秒
    double frame_delays = 1.0 / fps;
    AVFrame *frame = 0;
    //RGBA 4个
    //指针数组
    uint8_t *dst_data[4];
    int dst_linesize[4];
    //申请内存
    av_image_alloc(dst_data, dst_linesize,
                   avCodecContext->width, avCodecContext->height,
                   AV_PIX_FMT_RGBA, 1);
    while (isPlaying) {
        int ret = frames.pop(frame);
        if (!isPlaying) {
            break;
        }
        if (!ret) {
            break;
        }
        //src_linesize:表示每一行存放的字节大小
        sws_scale(swsContext, reinterpret_cast<const uint8_t *const *>(frame->data),
                  frame->linesize, 0, avCodecContext->height, dst_data, dst_linesize);
        //获取当前这一个画面播放的相对时间
        double clock = frame->best_effort_timestamp * av_q2d(time_base);
        //额外的间隔时间
        double extra_delay = frame->repeat_pict / (2 * fps);
        //真实的间隔时间
        double delays = extra_delay + frame_delays;
        if (!audioChannel) {
            av_usleep(delays * 1000 * 1000);
        } else {
            //休眠
            if (!clock) {
                av_usleep(delays * 1000 * 1000);
            } else {
                //比较音频与视频
                double audioClock = audioChannel->clock;
                LOGD("clock：%1f--audioClock：%1f",clock,audioClock);
                //间隔 大于0 视频快 反之 音频快
                double diff = clock - audioClock;
                if (diff > 0) {
                    LOGD("视频快了：%1f",diff);
                    av_usleep((delays + diff) * 1000 * 1000);
                } else if (diff < 0) {
                    LOGD("音频快了：%1f",diff);
                    //视频包挤压太多了，（丢包）
                    if (fabs(diff) > 0.05) {
                        //丢包（AVFrame）
                        frames.sync();
                        continue;
                    }else{
                        //不睡了 快点赶上音频
                    }
                }

            }
        }
        if(callback && avCodecContext) {
            callback(dst_data[0], dst_linesize[0], avCodecContext->width, avCodecContext->height);
        }
        releaseAVFrame(frame);
    }
    LOGE("dst_linesize[0]:");
    if(dst_data[0]) {
        av_freep(&dst_data[0]);
    }
//    if(dst_linesize[0]) {
//        av_freep(&dst_linesize[0]);
//    }
    releaseAVFrame(frame);
    isPlaying=0;
    if(swsContext) {
        sws_freeContext(swsContext);
        swsContext = 0;
    }
}

void VideoChannel::setRenderFrameCallback(RenderFrameCallback callback) {
    this->callback = callback;
}

void VideoChannel::stop() {
    isPlaying=0;
    frames.setWork(0);
    packets.setWork(0);
}
