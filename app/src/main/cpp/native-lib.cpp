#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_nsg_ndktest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C" JNIEXPORT jint JNICALL
Java_com_nsg_ndktest_MainActivity_add( JNIEnv *env, jobject, jint x, jint y) {

        //return an integer

        return x + y;
}

//Subtraction function
extern "C" JNIEXPORT jint JNICALL
Java_com_nsg_ndktest_MainActivity_sub( JNIEnv *env, jobject, jint x, jint y) {

        //return an integer
        return x - y;
}

//Multiplication function
extern "C" JNIEXPORT jint JNICALL
Java_com_nsg_ndktest_MainActivity_multiply( JNIEnv *env, jobject, jint x, jint y) {

        //return an integer
        return x * y;
}

//Division function
extern "C" JNIEXPORT jint JNICALL
 Java_com_nsg_ndktest_MainActivity_divide( JNIEnv *env, jobject, jint x, jint y) {

        //return an integer
        return x / y;
}
