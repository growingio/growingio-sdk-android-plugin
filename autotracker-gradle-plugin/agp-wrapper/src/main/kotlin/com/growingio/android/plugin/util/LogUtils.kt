/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.android.plugin.util

/**
 * <p>
 *
 * @author cpacm 2022/3/31
 */

var LOG_ENABLE = false

fun info(message: String) {
    if (LOG_ENABLE) {
        println("[GIO.Info] $message")
    }
}

fun g(message: String){
    if (LOG_ENABLE) {
        println("[GioKit] $message")
    }
}

fun w(message: String, cause: Throwable? = null) {
    if (LOG_ENABLE) {
        System.err.println("[GIO.warn] $message")
        cause?.printStackTrace(System.err)
    }
}

fun e(message: String, cause: Throwable? = null) {
    System.err.println("[GIO.error] $message")
    cause?.printStackTrace(System.err)
}