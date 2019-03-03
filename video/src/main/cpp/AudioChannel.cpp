//
// Created by 张清斌 on 2018/10/6.
//

#include "AudioChannel.h"

AudioChannel::AudioChannel(int id, AVCodecContext *context, AVRational time_base) : BaseChannel(id,
                                                                                                context,
                                                                                                time_base) {
    //44100个16位 44100*2
    //双声道 在*2
    out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    out_sampleSize = av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);
    out_sample_rate = 44100;
    data = static_cast<uint8_t *>(malloc(out_sample_rate * out_channels * out_sampleSize));
    memset(data, 0, out_sample_rate * out_channels * out_sampleSize);
}

AudioChannel::~AudioChannel() {
    if (data) {
        free(data);
        data = 0;
    }
}

//声明并且实现
void *audio_decode_task(void *args) {
    AudioChannel *channel = static_cast<AudioChannel *>(args);
    channel->decode();
    return 0;
}

void *audio_play_task(void *args) {
    AudioChannel *channel = static_cast<AudioChannel *>(args);
    channel->_play();
    return 0;
}

//返回获取的pcm数据的大小
int AudioChannel::getPcm() {
    int data_size = 0;
    AVFrame *frame;
    int ret = frames.pop(frame);
    if (!isPlaying) {
        if (ret) {
            releaseAVFrame(frame);
        }
        return data_size;
    }
    LOGD("AudioChannel::getPcm()");
    //重采样 48000Hz 8位 -> 44100Hz 16位 传过来的和要使用的格式不一样
    //假设我们输入了10个数据，swrContext转码器 这一次处理了8个数据
    //那么如果不加入delays(上次没处理完的数据)，积压，容易崩栈
    int64_t delays = swr_get_delay(swrContext, frame->sample_rate);
    //将 nb_ssamples个数据 由sample_rate采样率转成44100后返回多少个数据
    //10个48000=nb个44100
    // AV_ROUND_UP 向上取整 1.1 返回2
    int64_t max_samples = av_rescale_rnd(delays + frame->nb_samples, out_sample_rate,
                                         frame->sample_rate, AV_ROUND_UP);
    //上下文+输出缓冲区+输出缓冲区能接受的最大数据量+输入数据+输入数据个数
    //返回每一个声道输出的数据
    int samples = swr_convert(swrContext, &data, max_samples, (const uint8_t **) frame->data,
                              frame->nb_samples);
    //获得samples个 2字节（16位） *2声道
    data_size = samples * out_channels * out_sampleSize;
    //获取frame的一个相对播放时间
    //获得相对播放这一段数据的秒数
    clock = frame->pts * av_q2d(time_base);
    releaseAVFrame(frame);
    return data_size;
}

void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    AudioChannel *channel = static_cast<AudioChannel *>(context);
    //获得pcm数据有多少个字节
    int dataSize = channel->getPcm();
    if (dataSize > 0) {
        //接收16位数据
        (*bq)->Enqueue(bq, channel->data, dataSize);
    }
}

//PCM：声音的原始格式
//RGB,YUV 图像的原始格式
void AudioChannel::play() {
    //开始播放
    packets.setWork(1);
    frames.setWork(1);
    //0+输出声道+输出采样位+输出采样率+输入三个参数
    swrContext = swr_alloc_set_opts(0, AV_CH_LAYOUT_STEREO, AV_SAMPLE_FMT_S16, out_sample_rate,
                                    avCodecContext->channel_layout, avCodecContext->sample_fmt,
                                    avCodecContext->sample_rate, 0, 0);
    //初始化
    swr_init(swrContext);

    isPlaying = 1;
    //1，解码
    pthread_create(&pid_audio_decode, 0, audio_decode_task, this);
    //2，播放
    pthread_create(&pid_audio_play, 0, audio_play_task, this);
}

void AudioChannel::decode() {
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
        LOGD("AudioChannel::decode");
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

void AudioChannel::_play() {
    /**
     * 1，创建引擎并获取引擎接口
     */
    SLresult result;
    // 1.1 创建引擎 SLObjectItf engineObject
    result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
    //断言，可直接让应用崩溃
//    assert(SL_RESULT_SUCCESS != result);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 1.2 初始化引擎
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 1.3 通过engineObject获取引擎接口SLEngineItf engineInterface
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE,
                                           &engineInterface);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    /**
     * 2，设置混音器（礼堂之类效果）
     */
    // 2.1创建混音器SLObjectItf outputMixObject
    //需要修改传递指针：outputMixObject 否则engineInterface
    result = (*engineInterface)->CreateOutputMix(engineInterface, &outputMixObject, 0,
                                                 0, 0);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    // 2.2初始化混音器outputMixObject
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        return;
    }
    //不启用混响可以不用获取接口
    // 获得混音器接口
    //result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
    //                                         &outputMixEnvironmentalReverb);
    //if (SL_RESULT_SUCCESS == result) {
    //设置混响 ： 默认。
    //SL_I3DL2_ENVIRONMENT_PRESET_ROOM: 室内
    //SL_I3DL2_ENVIRONMENT_PRESET_AUDITORIUM : 礼堂 等
    //const SLEnvironmentalReverbSettings settings = SL_I3DL2_ENVIRONMENT_PRESET_DEFAULT;
    //(*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
    //       outputMixEnvironmentalReverb, &settings);
    //}
    /*
     * 3，创建播放器
     */
    //3.1 配置输入声音信息
    //创建buffer缓冲类型的队列 2个队列
    SLDataLocator_AndroidSimpleBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                                                            2};
    //pcm数据格式
    //pcm+2(双声道)+44100（采样率）+16（采样位）+16（数据的大小）+LEFT/RIGHT(双声道)+小端数据
    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM, 2, SL_SAMPLINGRATE_44_1, SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                            SL_BYTEORDER_LITTLEENDIAN};

    //数据源 将上述配置信息放到这个数据源中(输入声音的不同的格式)
    SLDataSource slDataSource = {&android_queue, &pcm};
    //3.2 配置音轨（输出）
    //设置混音器 播放器实际是通过混音器输出
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, NULL};
    //需要的接口 操作队列的接口 如果需要混音加上 SL_IID_EFFECTSEND(特效)
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    //3.3 创建播放器
    (*engineInterface)->CreateAudioPlayer(engineInterface, &bqPlayerObject, &slDataSource,
                                          &audioSnk, 1,
                                          ids, req);
    //初始化播放器
    (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);

//    得到接口后调用  获取Player接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerInterface);

    /**
     * 4,设置播放回调
     */
    //获取播放器队列接口
    (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
                                    &bqPlayerBufferQueueInterface);
    //设置回调
    (*bqPlayerBufferQueueInterface)->RegisterCallback(bqPlayerBufferQueueInterface,
                                                      bqPlayerCallback, this);
    /**
     * 5，设置播放状态
     */
    (*bqPlayerInterface)->SetPlayState(bqPlayerInterface, SL_PLAYSTATE_PLAYING);
    /**
     * 6，手动激活回调
     */
    bqPlayerCallback(bqPlayerBufferQueueInterface, this);
}

void AudioChannel::stop() {
    LOGD("AudioChannel:stop");
    isPlaying = 0;
    frames.setWork(0);
    packets.setWork(0);
    pthread_join(pid_audio_decode, 0);
    pthread_join(pid_audio_play, 0);
    if (swrContext) {
        swr_free(&swrContext);
        swrContext = 0;
    }
    //释放播放器
    if(bqPlayerObject){
        (*bqPlayerObject)->Destroy(bqPlayerObject);
        bqPlayerObject=0;
        bqPlayerInterface=0;
        bqPlayerBufferQueueInterface=0;
    }
    //释放混音器
    if(outputMixObject){
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject=0;
    }
    //释放引擎
    if(engineObject){
        (*engineObject)->Destroy(engineObject);
        engineObject=0;
        engineInterface=0;
    }
}



