/**
 * Copyright (C) 2020, ControlThings Oy Ab
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @license Apache-2.0
 */
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class mist_RequestInterface */

#ifndef _Included_mist_RequestInterface
#define _Included_mist_RequestInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     mist_RequestInterface
 * Method:    jniWishApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_RequestInterface_jniWishApiRequest
  (JNIEnv *, jobject, jstring, jbyteArray, jobject);

/*
 * Class:     mist_RequestInterface
 * Method:    jniMistApiRequest
 * Signature: (Ljava/lang/String;[BLmist/sandbox/Callback;)I
 */
JNIEXPORT jint JNICALL Java_mist_RequestInterface_jniMistApiRequest
  (JNIEnv *, jobject, jstring, jbyteArray, jobject);

/*
 * Class:     mist_RequestInterface
 * Method:    jniMistApiRequestCancel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mist_RequestInterface_jniMistApiRequestCancel
  (JNIEnv *, jobject, jint);

/*
 * Class:     mist_RequestInterface
 * Method:    isConnected
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mist_RequestInterface_isConnected
  (JNIEnv *, jobject);

/*
 * Class:     mist_RequestInterface
 * Method:    registerInstance
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mist_RequestInterface_registerInstance
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
