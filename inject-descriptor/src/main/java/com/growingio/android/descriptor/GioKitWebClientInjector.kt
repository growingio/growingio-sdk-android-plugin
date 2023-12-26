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

package com.growingio.android.descriptor

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.SuperInject

/**
 * <p>
 *     giokit web js 注入
 * @author cpacm 2023/9/8
 */
@Belong(className = "com.growingio.giokit.hook.GioWebView")
interface GioKitWebClientInjector {

    @SuperInject(
        clazz = WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [WebView::class, Int::class],
        isAfter = false
    )
    fun addCircleJsToWebView(webView: WebView, progress: Int)

    @SuperInject(
        clazz = com.tencent.smtt.sdk.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.tencent.smtt.sdk.WebView::class, Int::class],
        isAfter = false
    )
    fun addCircleJsToX5(webView: com.tencent.smtt.sdk.WebView, progress: Int)

    @SuperInject(
        clazz = com.uc.webview.export.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.uc.webview.export.WebView::class, Int::class],
        isAfter = false
    )
    fun addCircleJsToUc(webView: com.uc.webview.export.WebView, progress: Int)
}