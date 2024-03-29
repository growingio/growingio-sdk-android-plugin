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

import com.growingio.android.plugin.AutoTrackerExtension
import com.growingio.android.plugin.giokit.GioKitInjectData
import com.growingio.android.plugin.hook.HookClassesConfig

/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */
internal fun shouldClassModified(
    excludePackages: Array<String>, includePackages: Array<String>,
    className: String
): Boolean {
    if (isAndroidGenerated(className)) {
        return false
    }

    includePackages.forEach {
        if (className.startsWith(it)) {
            return true
        }
    }
    excludePackages.forEach {
        if (className.startsWith(it)) {
            return false
        }
    }
    INCLUDED_PACKAGES.forEach {
        if (className.startsWith(it)) {
            return true
        }
    }

    EXCLUDED_PACKAGES.forEach {
        if (className.startsWith(it)) {
            return false
        }
    }
    return true
}

val INCLUDED_PACKAGES = arrayListOf<String>()

val EXCLUDED_PACKAGES = arrayListOf(
    "com.growingio.android",
    "com.growingio.giokit",
    //"com.alibaba.mobileim.extra.xblink.webview",
    //"com.alibaba.sdk.android.feedback.xblink",
    //"com.tencent.smtt",
    //"android.taobao.windvane.webview",
    "com.sensorsdata.analytics", //sensorsdata
    "com.blueware.agent.android", //bluware
    "com.oneapm.agent.android", //OneAPM
    "com.networkbench.agent",//tingyun

    // OFFICIAL
    //"androidx",
    //"android.support",
    //"org.jetbrains.kotlin",
    "android.arch",
    "androidx.lifecycle.ReportFragment",
    //"androidx.navigation.fragment.NavHostFragment",
    //"com.google.android",

    //THIRD
    "com.bumptech.glide",
    "io.rectivex.rxjava",
    "com.baidu.location",
    "com.qiyukf",
    "com.tencent.smtt",
    "com.umeng.message",
    "com.xiaomi.push",
    "com.huawei.hms",
    "cn.jpush.android",
    "cn.jiguang",
    "com.meizu.cloud.pushsdk",
    "com.vivo.push",
    "com.igexin",
    "com.getui",
    "com.xiaomi.mipush.sdk",
    "com.heytap.msp.push",
    "com.tencent.tinker",
    "com.amap.api",
    "com.google.iot"
)

private val defaultInjectClass = arrayListOf(
    "com.growingio.android.sdk.autotrack.inject.ActivityInjector",
    "com.growingio.android.sdk.autotrack.inject.DialogInjector",
    "com.growingio.android.sdk.autotrack.inject.FragmentInjector",
    "com.growingio.android.sdk.autotrack.inject.FragmentV4Injector",
    "com.growingio.android.sdk.autotrack.inject.MenuItemInjector",
    "com.growingio.android.sdk.autotrack.inject.UcWebViewInjector",
    "com.growingio.android.sdk.autotrack.inject.WebViewInjector",
    "com.growingio.android.sdk.autotrack.inject.X5WebViewInjector",
    "com.growingio.android.sdk.autotrack.inject.ViewClickInjector",
    "com.growingio.android.sdk.autotrack.inject.ViewChangeInjector",
)

val ANALYTIC_ADAPTER_INJECT_CLASS = arrayListOf(
    "com.growingio.android.analytics.firebase.FirebaseAnalyticsInjector",
    "com.growingio.android.analytics.google.GoogleAnalyticsInjector",
    "com.growingio.android.analytics.sensor.SensorAnalyticsInjector"
)

val EXECUTE_INJECT_CLASS = arrayListOf<String>()

fun initInjectClass(extension: AutoTrackerExtension) {
    EXECUTE_INJECT_CLASS.clear()
    EXECUTE_INJECT_CLASS.addAll(defaultInjectClass)

    extension.injectClasses?.let {
        EXECUTE_INJECT_CLASS.addAll(it)
    }

    extension.analyticsAdapter?.apply {
        if (firebaseAnalytics) {
            EXECUTE_INJECT_CLASS.add(ANALYTIC_ADAPTER_INJECT_CLASS[0])
        }
        if (googleAnalytics) {
            EXECUTE_INJECT_CLASS.add(ANALYTIC_ADAPTER_INJECT_CLASS[1])
        }
        if (sensorAnalytics) {
            EXECUTE_INJECT_CLASS.add(ANALYTIC_ADAPTER_INJECT_CLASS[2])
            INCLUDED_PACKAGES.add("com.sensorsdata.analytics.android.sdk.SensorsDataAPI")
            INCLUDED_PACKAGES.add("com.sensorsdata.analytics.android.sdk.AbstractSensorsDataAPI")
            INCLUDED_PACKAGES.add("com.sensorsdata.analytics.android.sdk.core.event.imp.TrackEventAssemble")
        } else {
            INCLUDED_PACKAGES.remove("com.sensorsdata.analytics.android.sdk.SensorsDataAPI")
            INCLUDED_PACKAGES.remove("com.sensorsdata.analytics.android.sdk.AbstractSensorsDataAPI")
            INCLUDED_PACKAGES.remove("com.sensorsdata.analytics.android.sdk.core.event.imp.TrackEventAssemble")
        }
    }

    extension.giokit?.apply {
        if (this.enabled) {
            EXECUTE_INJECT_CLASS.addAll(GioKitInjectData.GIOKIT_INJECT_CLASS)
        }
    }

    HookClassesConfig.initDefaultInjector(EXECUTE_INJECT_CLASS)
}
