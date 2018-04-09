#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_hadroncfy_project4_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jint JNICALL Java_com_hadroncfy_project4_Cryptoc_encryptFile(JNIEnv *env, jclass ceci, jstring inFile, jstring outFile, jstring passwd){}