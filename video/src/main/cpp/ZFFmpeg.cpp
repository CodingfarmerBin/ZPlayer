//
// Created by 张清斌 on 2018/10/6.
//

#include <cstring>
#include "ZFFmpeg.h"
#include "macro.h"
#include <pthread.h>

extern "C" {
#include <libavutil/time.h>
}

void *task_prepare(void *args) {
    ZFFmpeg *ffmpeg = static_cast<ZFFmpeg *>(args);
    ffmpeg->_prepare();
    return 0;
}

ZFFmpeg::ZFFmpeg(JavaCallHelper *callHelper, const char *dataSource) {
    this->callHelper = callHelper;
    //防止dataSource 指向的内存被释放，悬空指针
    // strlen获取字符串的长度 不包括\0
    this->dataSource = new char[strlen(dataSource) + 1];
    strcpy(this->dataSource, dataSource);
}

ZFFmpeg::~ZFFmpeg() {
    DELETE(dataSource)
}

void ZFFmpeg::prepare() {
    pthread_create(&pid_prepare, 0, task_prepare, this);
}

void ZFFmpeg::_prepare() {

    //初始化网络，让ffmpeg能够使用网络
    avformat_network_init();
    //1,打开媒体地址
    //AVFormatContext包含了视频的信息（宽高等）
    //文件路径不对，没网  ret返回1
    //参数3：指示打开的媒体格式（传NULL,ffmpeg就会自动推导出是MP4或者flv）
    //设置超时时间 微妙 超时时间5秒
    AVDictionary *options = 0;
    av_dict_set(&options, "timeout", "10000000", 0);
    int ret = avformat_open_input(&formatContext, dataSource, 0, &options);
    av_dict_free(&options);
    if (ret != 0) {
        LOGE("打开媒体失败:%s", av_err2str(ret));
        if (callHelper) {
            callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_OPEN_URL);
        }
        return;
    }
    //2,查找媒体中的音视频流
    ret = avformat_find_stream_info(formatContext, 0);
    if (ret < 0) {
        LOGE("2,查找媒体中的音视频流失败:%s", av_err2str(ret));
        if (callHelper) {
            callHelper->onError(THREAD_CHILD, FFMPEG_CAN_NOT_FIND_STREAMS);
        }
        return;
    }
    //输入视频的AVStream个数  一般为 2 （几段视频/音频）
    for (int i = 0; i < formatContext->nb_streams; ++i) {
        //可能代表是一个视频也可能是一个音频
        AVStream *stream = formatContext->streams[i];
        //包含了解码这段流的各种参数信息
        AVCodecParameters *codecpar = stream->codecpar;
        //无论音频还是视频都需要干的事情（获得解码器）
        //1，查找当前流使用的编码方法，查找解码器
        AVCodec *codec = avcodec_find_decoder(codecpar->codec_id);
        if (codec == NULL) {
            LOGE("查找解码器失败:%s", av_err2str(ret));
            if (callHelper) {
                callHelper->onError(THREAD_CHILD, FFMPEG_FIND_DECODER_FAIL);
            }
            return;
        }
        //2,获得解码器上下文
        AVCodecContext *context3 = avcodec_alloc_context3(codec);
        if (context3 == NULL) {
            LOGE("获得解码器上下文失败:%s", av_err2str(ret));
            if (callHelper) {
                callHelper->onError(THREAD_CHILD, FFMPEG_ALLOC_CODEC_CONTEXT_FAIL);
            }
            return;
        }
        //3,设置上下文内的一些参数
        ret = avcodec_parameters_to_context(context3, codecpar);
        if (ret < 0) {
            LOGE("设置上下文内的一些参数失败:%s", av_err2str(ret));
            if (callHelper) {
                callHelper->onError(THREAD_CHILD, FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL);
            }
            return;
        }
        //4,打开解码器
        ret = avcodec_open2(context3, codec, 0);
        if (ret != 0) {
            LOGE("打开解码器失败:%s", av_err2str(ret));
            if (callHelper) {
                callHelper->onError(THREAD_CHILD, FFMPEG_OPEN_DECODER_FAIL);
            }
            return;
        }
        //单位
        AVRational time_base = stream->time_base;
        //音频
        if (codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audioChannel = new AudioChannel(i, context3, time_base);
        } else if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {//视频
            //帧率：单位时间内能够显示多少个图像
            AVRational rational = stream->avg_frame_rate;
            //av_q2d : rational.num/rational.den;
            int fps = av_q2d(rational);

            videoChannel = new VideoChannel(i, context3, time_base, fps);
            videoChannel->setRenderFrameCallback(callback);
        }

    }
    //没有音视频
    if (!audioChannel && !videoChannel) {
        LOGE("没有音视频");
        if (callHelper) {
            callHelper->onError(THREAD_CHILD, FFMPEG_NOMEDIA);
        }
        return;
    }

    //准备完了，通知java随时可以开始播放
    if (callHelper) {
        callHelper->onPrepare(THREAD_CHILD);
    }
}

void *play(void *args) {
    ZFFmpeg *ffmpeg = static_cast<ZFFmpeg *>(args);
    ffmpeg->_start();
    return 0;
};

void ZFFmpeg::start() {
    isPlaying = 1;
    if (audioChannel) {//启动声音的播放
        audioChannel->play();
    }
    if (videoChannel) {
        if (audioChannel) {
            videoChannel->setAudioChannel(audioChannel);
        }
        videoChannel->play();
    }

    pthread_create(&pid_play, 0, play, this);
}

//只负责读取数据包
void ZFFmpeg::_start() {
    //1，读取媒体数据包（音视频数据包）
    //avFrame编码前的数据  avPacket 编码后的数据
    LOGE("--_start-->%ld", pid_play);
    int ret;
    while (isPlaying) {
        //读取文件的时候没有网络请求，一下子读完，可能导致oom
        if (audioChannel && audioChannel->packets.size() > 100) {
            av_usleep(1000 * 10);
            continue;
        }
        if (videoChannel && videoChannel->packets.size() > 100) {
            av_usleep(1000 * 10);
            continue;
        }
        //av_packet_alloc 申请内存在堆当中
        AVPacket *packet = av_packet_alloc();
        ret = av_read_frame(formatContext, packet);
        //0成功
        if (ret == 0) {
            //一个流的下标 序号
            if (audioChannel && packet->stream_index == audioChannel->id) {//音频包
                audioChannel->packets.push(packet);
            } else if (videoChannel && packet->stream_index == videoChannel->id) {//视频包
                videoChannel->packets.push(packet);
            }
        } else if (ret == AVERROR_EOF) {//读取完成但是可能没有播放完 缓冲
            if (audioChannel->packets.empty() && audioChannel->frames.empty() &&
                videoChannel->packets.empty() && videoChannel->frames.empty()) {
                break;
            }
            //为什么这里要他继续循环 而不是 sleep
            //如果是直播，可以sleep
            //如果支持点播（播放本地文件） seek后退（进度条拖拽）
        } else {
            break;
        }
    }
    isPlaying = 0;
    audioChannel->stop();
    videoChannel->stop();
}

void ZFFmpeg::setRenderFrameCallback(RenderFrameCallback callback) {
    this->callback = callback;
}

void *async_stop(void *args) {
    ZFFmpeg *ffmpeg = static_cast<ZFFmpeg *>(args);
    int kill_rc = pthread_kill(ffmpeg->pid_play, 0);
    if (kill_rc == ESRCH || kill_rc == 0) {
        return 0;
    }
    //等待prepare结束
    pthread_join(ffmpeg->pid_prepare, 0);
    //等待播放结束 保证 start线程结束
    pthread_join(ffmpeg->pid_play, 0);
    DELETE(ffmpeg->videoChannel);
    DELETE(ffmpeg->audioChannel);
    if (ffmpeg->formatContext) {
        //先关闭读取（关闭 fileinputstream）
        avformat_close_input(&ffmpeg->formatContext);
        avformat_free_context(ffmpeg->formatContext);
        ffmpeg->formatContext = 0;
    }

    DELETE(ffmpeg) ;
    return 0;
}

void ZFFmpeg::stop() {
    isPlaying = 0;
    callHelper = 0;
//    if (audioChannel) {
//        audioChannel->callback = 0;
//    }
//    if (videoChannel) {
//        videoChannel->callback = 0;
//    }
    pthread_create(&pid_stop, 0, async_stop, this);
}

