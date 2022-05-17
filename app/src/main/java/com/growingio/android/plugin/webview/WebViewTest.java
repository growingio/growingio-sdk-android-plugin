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

package com.growingio.android.plugin.webview;

import android.content.Context;
import android.webkit.WebView;
import androidx.annotation.NonNull;

import com.growingio.android.sdk.autotrack.inject.WebViewInjector;

import java.util.HashMap;

/**
 * <p>
 *
 * @author cpacm 2022/5/12
 */
public class WebViewTest extends WebView {

    public WebViewTest(@NonNull Context context) {
        super(context);
    }

    private void loadUrl1() {
        loadUrl("https://www.cpacm.net");
    }

    private void loadUrl2() {
        HashMap<String, String> header = new HashMap<>();
        header.put("name", "cpacm");
        loadUrl("https://www.cpacm.net", header);
    }

    private void loadUrl3() {
        loadData("this is test data", "application/json", "UTF-8");
    }

    private void loadUrl4() {
        loadDataWithBaseURL("https://www.cpacm.net", "this is test data", "application/json", "UTF-8", "https://cpacm.8bgm.com");
    }
}
