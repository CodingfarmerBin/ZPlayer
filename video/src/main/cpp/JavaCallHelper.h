//
// Created by 张清斌 on 2018/10/6.
//

#ifndef ZPLAYER_JAVACALLHELPER_H
#define ZPLAYER_JAVACALLHELPER_H


#include <jni.h>

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM *vm,JNIEnv* env,jobject instance);
    ~JavaCallHelper();

    void onError(int thread,int errorCode);
    void onPrepare(int thread);

private:
    JavaVM *vm;
    JNIEnv* env;
    jobject instance;
    jmethodID onErrorId;
    jmethodID onPrepareId;
};


#endif //ZPLAYER_JAVACALLHELPER_H
