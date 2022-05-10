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

import com.growingio.inject.annotation.Before
import com.growingio.inject.annotation.Belong
import com.uc.webview.export.WebView

/**
 * <p>
 *     UCWebView注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.UcWebViewInjector")
interface UcWebViewInjector {
    @Before(clazz = WebView::class, method = "loadUrl", parameterTypes = [String::class])
    fun ucWebViewLoadUrl(webView: WebView, url: String)

    @Before(clazz = WebView::class, method = "loadUrl", parameterTypes = [String::class, MutableMap::class])
    fun ucWebViewLoadUrl(webView: WebView, url: String?, additionalHttpHeaders: Map<String?, String?>?)

    @Before(clazz = WebView::class, method = "loadData", parameterTypes = [String::class, String::class, String::class])
    fun ucWebViewLoadData(webView: WebView, data: String?, mimeType: String?, encoding: String?)

    @Before(
        clazz = WebView::class,
        method = "loadDataWithBaseURL",
        parameterTypes = [String::class, String::class, String::class, String::class, String::class]
    )
    fun ucWebViewLoadDataWithBaseURL(
        webView: WebView,
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    )

    @Before(clazz = WebView::class, method = "postUrl", parameterTypes = [String::class, ByteArray::class])
    fun ucWebViewPostUrl(webView: WebView, url: String?, postData: ByteArray?)
}