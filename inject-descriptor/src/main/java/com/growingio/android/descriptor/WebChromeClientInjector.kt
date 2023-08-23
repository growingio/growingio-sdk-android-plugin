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
import com.growingio.inject.annotation.AroundInject
import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.SuperInject

/**
 * <p>
 *     WebChromeClient 注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.WebChromeClientInjector")
interface WebChromeClientInjector {

    @SuperInject(
        clazz = WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [WebView::class, Int::class],
        isAfter = false
    )
    fun onProgressChangedStart(webView: WebView, progress: Int)

    @SuperInject(
        clazz = WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [WebView::class, Int::class],
        isAfter = true
    )
    fun onProgressChangedEnd(webView: WebView, progress: Int)


    @SuperInject(
        clazz = com.tencent.smtt.sdk.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.tencent.smtt.sdk.WebView::class, Int::class],
        isAfter = false
    )
    fun onX5ProgressChangedStart(webView: com.tencent.smtt.sdk.WebView, progress: Int)

    @SuperInject(
        clazz = com.tencent.smtt.sdk.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.tencent.smtt.sdk.WebView::class, Int::class],
        isAfter = true
    )
    fun onX5ProgressChangedEnd(webView: com.tencent.smtt.sdk.WebView, progress: Int)

    @SuperInject(
        clazz = com.uc.webview.export.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.uc.webview.export.WebView::class, Int::class],
        isAfter = false
    )
    fun onUcProgressChangedStart(webView: com.uc.webview.export.WebView, progress: Int)

    @SuperInject(
        clazz = com.uc.webview.export.WebChromeClient::class,
        method = "onProgressChanged",
        parameterTypes = [com.uc.webview.export.WebView::class, Int::class],
        isAfter = true
    )
    fun onUcProgressChangedEnd(webView: com.uc.webview.export.WebView, progress: Int)


    @AroundInject(
        clazz = WebView::class,
        method = "setWebChromeClient",
        parameterTypes = [WebChromeClient::class]
    )
    fun setWebChromeClient(webView: WebView, webChromeClient: WebChromeClient)

    @AroundInject(
        clazz = com.tencent.smtt.sdk.WebView::class,
        method = "setWebChromeClient",
        parameterTypes = [com.tencent.smtt.sdk.WebChromeClient::class]
    )
    fun setX5WebChromeClient(
        webView: com.tencent.smtt.sdk.WebView,
        webChromeClient: com.tencent.smtt.sdk.WebChromeClient
    )

    @AroundInject(
        clazz = com.uc.webview.export.WebView::class,
        method = "setWebChromeClient",
        parameterTypes = [com.uc.webview.export.WebChromeClient::class]
    )
    fun setUcWebChromeClient(
        webView: com.uc.webview.export.WebView,
        webChromeClient: com.uc.webview.export.WebChromeClient
    )


}