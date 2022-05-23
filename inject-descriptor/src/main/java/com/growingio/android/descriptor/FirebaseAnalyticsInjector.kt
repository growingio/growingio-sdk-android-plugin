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

import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.Inject

/**
 * <p>
 *
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.analytics.FirebaseAnalyticsInjector")
interface FirebaseAnalyticsInjector {

    @Inject(
        targetClazz = "com/google/firebase/analytics/FirebaseAnalytics",
        targetMethod = "logEvent",
        targetMethodDesc = "(Ljava/lang/String;Landroid/os/Bundle;)V",
        injectMethod = "logEvent",
        injectMethodDesc = "(Ljava/lang/String;Landroid/os/Bundle;)V",
        isAfter = true,
        isSuper = true
    )
    fun logEvent()

    @Inject(
        targetClazz = "com/google/firebase/analytics/FirebaseAnalytics",
        targetMethod = "setDefaultEventParameters",
        targetMethodDesc = "(Landroid/os/Bundle;)V",
        injectMethod = "setDefaultEventParameters",
        injectMethodDesc = "(Landroid/os/Bundle;)V",
        isAfter = true,
        isSuper = true
    )
    fun setDefaultEventParameters()

    @Inject(
        targetClazz = "com/google/firebase/analytics/FirebaseAnalytics",
        targetMethod = "setUserId",
        targetMethodDesc = "(Ljava/lang/String;)V",
        injectMethod = "setUserId",
        injectMethodDesc = "(Ljava/lang/String;)V",
        isAfter = true,
        isSuper = true
    )
    fun setUserId()

    @Inject(
        targetClazz = "com/google/firebase/analytics/FirebaseAnalytics",
        targetMethod = "setUserProperty",
        targetMethodDesc = "(Ljava/lang/String;Ljava/lang/String;)V",
        injectMethod = "setUserProperty",
        injectMethodDesc = "(Ljava/lang/String;Ljava/lang/String;)V",
        isAfter = true,
        isSuper = true
    )
    fun setUserProperty();
}