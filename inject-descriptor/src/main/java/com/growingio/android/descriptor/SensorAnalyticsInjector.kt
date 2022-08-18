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
@Belong(className = "com.growingio.android.analytics.sensor.SensorAnalyticsInjector")
interface SensorAnalyticsInjector {

    @Inject(
        targetClazz = "com/sensorsdata/analytics/android/sdk/SensorsDataAPI",
        targetMethod = "disableSDK",
        targetMethodDesc = "()V",
        injectMethod = "disableSDK",
        injectMethodDesc = "()V",
        isAfter = false,
        type = 0
    )
    fun disableSDK()

    @Inject(
        targetClazz = "com/sensorsdata/analytics/android/sdk/SensorsDataAPI",
        targetMethod = "enableSDK",
        targetMethodDesc = "()V",
        injectMethod = "enableSDK",
        injectMethodDesc = "()V",
        isAfter = false,
        type = 0
    )
    fun enableSDK()

    @Inject(
        targetClazz = "com/sensorsdata/analytics/android/sdk/AbstractSensorsDataAPI",
        targetMethod = "trackEvent",
        targetMethodDesc = "(Lcom/sensorsdata/analytics/android/sdk/internal/beans/EventType;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V",
        injectMethod = "trackEvent",
        injectMethodDesc = "(Lcom/sensorsdata/analytics/android/sdk/internal/beans/EventType;Ljava/lang/String;Lorg/json/JSONObject;Ljava/lang/String;)V",
        isAfter = false,
        type = 0
    )
    fun trackEvent()
}