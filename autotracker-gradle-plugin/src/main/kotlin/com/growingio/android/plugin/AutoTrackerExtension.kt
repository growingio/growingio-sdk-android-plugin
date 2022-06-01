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

package com.growingio.android.plugin

import org.gradle.api.Action

/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */

open class AutoTrackerExtension {
    var logEnabled = false

    var skipDependencyCheck = false

    var excludePackages: Array<String>? = null

    var includePackages: Array<String>? = null

    var injectClasses: Array<String>? = null

    var analyticsAdapter: AnalyticsAdapter? = null

    open fun analyticsAdapter(configuration: Action<in AnalyticsAdapter>) {
        analyticsAdapter = AnalyticsAdapter().apply { configuration.execute(this) }
    }
}

//用于配置是否可以对第三方分析服务进行适配
open class AnalyticsAdapter (
    // 针对 FirebaseAnalytics
    var firebaseAnalytics: Boolean = false,
    // 针对 GoogleAnalytics
    var googleAnalytics: Boolean = false,
)