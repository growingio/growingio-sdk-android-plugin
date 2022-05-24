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

import com.growingio.inject.annotation.AroundInject
import com.growingio.inject.annotation.Belong
import com.tencent.smtt.sdk.WebView

/**
 * <p>
 *     X5WebView注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.X5WebViewInjector")
interface X5WebViewInjector {
    @AroundInject(clazz = WebView::class, method = "loadUrl", parameterTypes = [String::class])
    fun x5WebViewLoadUrl(webView: WebView, url: String?)

    @AroundInject(clazz = WebView::class, method = "loadUrl", parameterTypes = [String::class, MutableMap::class])
    fun x5WebViewLoadUrl(webView: WebView, url: String?, additionalHttpHeaders: Map<String?, String?>?)

    @AroundInject(clazz = WebView::class, method = "loadData", parameterTypes = [String::class, String::class, String::class])
    fun x5WebViewLoadData(webView: WebView, data: String?, mimeType: String?, encoding: String?)

    @AroundInject(
        clazz = WebView::class,
        method = "loadDataWithBaseURL",
        parameterTypes = [String::class, String::class, String::class, String::class, String::class]
    )
    fun x5WebViewLoadDataWithBaseURL(
        webView: WebView,
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    )

    @AroundInject(clazz = WebView::class, method = "postUrl", parameterTypes = [String::class, ByteArray::class])
    fun x5WebViewPostUrl(webView: WebView, url: String?, postData: ByteArray?)
}