package com.growingio.android.descriptor

import com.growingio.inject.annotation.Belong
import com.growingio.inject.annotation.Inject

@Belong(className = "com.growingio.android.analytics3.GoogleAnalyticsInjector")
interface GoogleAnalyticsInjector {

    @Inject(
        targetClazz = "com/google/android/gms/analytics/GoogleAnalytics",
        targetMethod = "newTracker",
        targetMethodDesc = "(I)Lcom/google/android/gms/analytics/Tracker;",
        injectMethod = "newTracker",
        injectMethodDesc = "(Lcom/google/android/gms/analytics/Tracker;Lcom/google/android/gms/analytics/GoogleAnalytics;I)V",
        isAfter = true,
        type = 0
    )
    fun newTracker(resId: Int)

    @Inject(
        targetClazz = "com/google/android/gms/analytics/GoogleAnalytics",
        targetMethod = "newTracker",
        targetMethodDesc = "(Ljava/lang/String;)Lcom/google/android/gms/analytics/Tracker;",
        injectMethod = "newTracker",
        injectMethodDesc = "(Lcom/google/android/gms/analytics/Tracker;Lcom/google/android/gms/analytics/GoogleAnalytics;Ljava/lang/String;)V",
        isAfter = true,
        type = 0
    )
    fun newTracker(measurementId: String)

    @Inject(
        targetClazz = "com/google/android/gms/analytics/Tracker",
        targetMethod = "set",
        targetMethodDesc = "(Ljava/lang/String;Ljava/lang/String;)V",
        injectMethod = "set",
        injectMethodDesc = "(Lcom/google/android/gms/analytics/Tracker;Ljava/lang/String;Ljava/lang/String;)V",
        isAfter = true,
        type = 0
    )
    fun set()

    @Inject(
        targetClazz = "com/google/android/gms/analytics/Tracker",
        targetMethod = "send",
        targetMethodDesc = "(Ljava/util/Map;)V",
        injectMethod = "send",
        injectMethodDesc = "(Lcom/google/android/gms/analytics/Tracker;Ljava/util/Map;)V",
        isAfter = true,
        type = 0
    )
    fun send()

    @Inject(
        targetClazz = "com/google/android/gms/analytics/Tracker",
        targetMethod = "setClientId",
        targetMethodDesc = "(Ljava/lang/String;)V",
        injectMethod = "setClientId",
        injectMethodDesc = "(Lcom/google/android/gms/analytics/Tracker;Ljava/lang/String;)V",
        isAfter = true,
        type = 0
    )
    fun setClientId()
}