//
// Created by cfy on 18-3-29.
//
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "cryptoc.h"

static int keyScheme(const char *passwd, uint64_t len, uint8_t key[256]){
    crypto_SHA256((const uint8_t *)passwd, len, key);
}
static CryptoArg *getCryptArg(const uint8_t *key, const char *mode){
    CryptoArg *ret;
    if(!strcmp(mode, "AES-128")){
        ret = (CryptoArg *)malloc(sizeof(CryptoAESArg));
        crypto_AES_init((CryptoAESArg *)ret, key, CRYPTO_AES_KEY_128);
    }
    else if(!strcmp(mode, "AES-192")){
        ret = (CryptoArg *)malloc(sizeof(CryptoAESArg));
        crypto_AES_init((CryptoAESArg *)ret, key, CRYPTO_AES_KEY_192);
    }
    else if(!strcmp(mode, "AES-256")){
        ret = (CryptoArg *)malloc(sizeof(CryptoAESArg));
        crypto_AES_init((CryptoAESArg *)ret, key, CRYPTO_AES_KEY_256);
    }
    else if(!strcmp(mode, "DES")){
        ret = (CryptoArg *)malloc(sizeof(CryptoDESArg));
        crypto_DES_init((CryptoDESArg *)ret, key);
    }
    else if(!strcmp(mode, "SM4")){
        ret = (CryptoArg *)malloc(sizeof(Crypto_SM4Arg));
        crypto_SM4_init((Crypto_SM4Arg *)ret, key);
    }
    else {
        ret = NULL;
    }
    return ret;
}

JNIEXPORT jint JNICALL Java_com_hadroncfy_project4_Cryptoc_encryptFile(JNIEnv *env, jclass ceci, jstring inFile, jstring outFile, jstring mode, jstring passwd){
    jboolean b;
    FILE *in = fopen((*env)->GetStringUTFChars(env, inFile, &b), "ro");
    FILE *out = fopen((*env)->GetStringUTFChars(env, outFile, &b), "wo");
    uint8_t sk[256];
    keyScheme((*env)->GetStringUTFChars(env, passwd, &b), (*env)->GetStringLength(env, passwd), sk);
    CryptoArg *arg = getCryptArg(sk, (*env)->GetStringUTFChars(env, mode, &b));
    if(arg == NULL){
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalArgumentException"), "Unsupported encryption mode");
        return -1;
    }
    crypto_encrypt(in, out, arg);
    free(arg);
    fclose(in);
    fclose(out);
    return 0;
}

JNIEXPORT jint JNICALL Java_com_hadroncfy_project4_Cryptoc_decryptFile(JNIEnv *env, jclass ceci, jstring inFile, jstring outFile, jstring passwd){
    jboolean b;
    FILE *in = fopen((*env)->GetStringUTFChars(env, inFile, &b), "ro");
    FILE *out = fopen((*env)->GetStringUTFChars(env, outFile, &b), "wo");
    uint8_t sk[256];
    keyScheme((*env)->GetStringUTFChars(env, passwd, &b), (*env)->GetStringLength(env, passwd), sk);
    crypto_decrypt(in, out, sk);
    fclose(in);
    fclose(out);
    return 0;
}