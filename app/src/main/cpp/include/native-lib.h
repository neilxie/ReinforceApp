//
// Created by Administrator on 2016/12/7.
//

#ifndef APPLOADER_NATIVE_LIB_H_H
#define APPLOADER_NATIVE_LIB_H_H

#include <jni.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "AppLoader", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "AppLoader", __VA_ARGS__)
//#define LOGD(...)
//#define LOGE(...)
const int MAX_FILENAME_LEN = 256;

char *GetApkFilePath(JNIEnv *env, jobject app);
char *GetApkFileName(const char *apkFilePath);
char* Jstring2CStr(JNIEnv *env, jstring jstr);
int CreateDir(const char *pDir);
void LoadResource(JNIEnv *env, jstring apkFileName);
jobject createGameApplication(JNIEnv *env, jstring appName, jstring apkFileName);
void Java_com_jd_apploader_App_onAppCreate(JNIEnv *, jobject, jobject, jobject, jstring);
jobject Java_com_jd_apploader_App_onAppAttach(JNIEnv *, jobject, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif //APPLOADER_NATIVE_LIB_H_H
