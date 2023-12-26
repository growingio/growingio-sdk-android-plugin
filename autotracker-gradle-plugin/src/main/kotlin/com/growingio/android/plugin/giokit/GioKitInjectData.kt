package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.g
import com.growingio.android.plugin.util.normalize
import kotlin.reflect.full.isSubclassOf

/**
 * <p>
 *
 * @author cpacm 2023/9/7
 */
internal sealed class GioKitInjectData(
    val targetClassName: String,
    val targetMethodName: String,
    val targetMethodDesc: String,
    val injectClassName: String,
    val injectMethodName: String,
    val injectMethodDesc: String
) {
    companion object {
        /**
         * not support support.fragment and system fragment
         */
        val DEFAULT_AUTOTRACKER_CALLED_METHOD = hashSetOf(
            "com.growingio.android.sdk.autotrack.Autotracker#trackCustomEvent",
            "com.growingio.android.sdk.autotrack.Autotracker#trackEditText",
            "com.growingio.android.sdk.autotrack.Autotracker#trackViewImpression",
            "com.growingio.android.sdk.autotrack.Autotracker#stopTrackViewImpression",
            "com.growingio.android.sdk.autotrack.Autotracker#autotrackPage",
            "com.growingio.android.sdk.autotrack.Autotracker#ignoreView",
            "com.growingio.android.sdk.autotrack.Autotracker#ignoreClickView",
            "com.growingio.android.sdk.autotrack.Autotracker#setUniqueTag",
            "com.growingio.android.sdk.autotrack.Autotracker#setPageAttributes",
            "com.growingio.android.sdk.track.Tracker#trackCustomEvent",
            "com.growingio.android.sdk.track.Tracker#bridgeWebView",
            "com.growingio.android.sdk.track.Tracker#setLoginUserId",
            "com.growingio.android.sdk.track.Tracker#doDeepLinkByUrl",
            "com.growingio.android.sdk.track.Tracker#setLoginUserAttributes",
            "com.growingio.android.sdk.track.Tracker#setLocation",
            "com.growingio.android.sdk.track.Tracker#trackTimerEnd",
        )

        val GIOKIT_INJECT_CLASS = arrayListOf(
            "com.growingio.giokit.hook.GioWebView"
        )


        fun shouldClassModified(className: String): Boolean {
            val size = GioKitInjectData::class.nestedClasses
                .filter { kClass -> kClass.isSubclassOf(GioKitInjectData::class) }
                .map { klass -> klass.objectInstance }
                .filterIsInstance<GioKitInjectData>()
                .filter { it.matchTargetClass(className) }
                .size
            return size > 0
        }

        fun matchGioKitData(className: String, method: String, methodDesc: String): GioKitInjectData? {
            return GioKitInjectData::class.nestedClasses
                .filter { kClass -> kClass.isSubclassOf(GioKitInjectData::class) }
                .map { klass -> klass.objectInstance }
                .filterIsInstance<GioKitInjectData>()
                .firstOrNull { it.match(className, method, methodDesc) }
        }
    }

    fun matchTargetClass(className: String): Boolean {
        if (normalize(targetClassName) == className) {
            return true
        }
        return false
    }

    fun match(className: String, method: String, methodDesc: String): Boolean {
        if (matchTargetClass(className)) {
            val result = if (targetMethodDesc.isEmpty()) {
                method == targetMethodName
            } else {
                method == targetMethodName && methodDesc == targetMethodDesc
            }
            return result
        }
        return false
    }


    object GioKitInjectInit : GioKitInjectData(
        targetClassName = "com/growingio/android/sdk/Tracker",
        targetMethodName = "startAfterSdkSetup",
        targetMethodDesc = "(Lcom/growingio/android/sdk/TrackerContext;)V",
        injectClassName = "com/growingio/giokit/hook/GioPluginConfig",
        injectMethodName = "inject",
        injectMethodDesc = "(Landroid/content/Context;Ljava/util/Map;)V",
    )


    object GioKitInjectOkhttpV3 : GioKitInjectData(
        targetClassName = "com/growingio/android/okhttp3/OkHttpDataLoader\$Factory",
        targetMethodName = "getsInternalClient",
        targetMethodDesc = "(Lcom/growingio/android/okhttp3/OkHttpConfig;)Lokhttp3/Call\$Factory;",
        injectClassName = "com/growingio/giokit/hook/GioHttpCaptureInterceptor",
        injectMethodName = "<init>",
        injectMethodDesc = "()V",
    )

    object GioKitInjectUrlConn : GioKitInjectData(
        targetClassName = "com/growingio/android/urlconnection/UrlConnectionFetcher",
        targetMethodName = "loadDataWithRedirects",
        targetMethodDesc = "",
        injectClassName = "com/growingio/giokit/hook/GioHttp",
        injectMethodName = "parseGioKitUrlConnection",
        injectMethodDesc = "(Ljava/net/HttpURLConnection;Ljava/util/Map;[B)V",
    )

    object GioKitInjectVolleySuccess : GioKitInjectData(
        targetClassName = "com/growingio/android/volley/VolleyDataFetcher\$GioRequest",
        targetMethodName = "parseNetworkResponse",
        targetMethodDesc = "(Lcom/android/volley/NetworkResponse;)V",
        injectClassName = "com/growingio/giokit/hook/GioHttp",
        injectMethodName = "parseGioKitVolleySuccess",
        injectMethodDesc = "(Lcom/android/volley/Request;Lcom/android/volley/NetworkResponse;)V",
    )

    object GioKitInjectVolleyFail : GioKitInjectData(
        targetClassName = "com/growingio/android/volley/VolleyDataFetcher\$GioRequest",
        targetMethodName = "parseNetworkError",
        targetMethodDesc = "(Lcom/android/volley/VolleyError;)V",
        injectClassName = "com/growingio/giokit/hook/GioHttp",
        injectMethodName = "parseGioKitVolleyError",
        injectMethodDesc = "(Lcom/android/volley/Request;Lcom/android/volley/VolleyError;)V",
    )

    object GioKitInjectDatabaseInsert : GioKitInjectData(
        targetClassName = "com/growingio/android/database/EventDataManager",
        targetMethodName = "insertEvents",
        targetMethodDesc = "(Ljava/util/List;)I",
        injectClassName = "com/growingio/giokit/hook/GioDatabase",
        injectMethodName = "insertEvent",
        injectMethodDesc = "(Landroid/net/Uri;Lcom/growingio/android/sdk/track/middleware/GEvent;)V",
    )

    object GioKitInjectDatabaseOverdue : GioKitInjectData(
        targetClassName = "com/growingio/android/database/EventDataManager",
        targetMethodName = "removeOverdueEvents",
        targetMethodDesc = "()I",
        injectClassName = "com/growingio/giokit/hook/GioDatabase",
        injectMethodName = "outdatedEvents",
        injectMethodDesc = "()V",
    )

    object GioKitInjectDatabaseDeleteId : GioKitInjectData(
        targetClassName = "com/growingio/android/database/EventDataManager",
        targetMethodName = "removeEventById",
        targetMethodDesc = "(Landroid/content/ContentProviderClient;J)V",
        injectClassName = "com/growingio/giokit/hook/GioDatabase",
        injectMethodName = "deleteEvent",
        injectMethodDesc = "(J)V",
    )

    object GioKitInjectDatabaseRemove : GioKitInjectData(
        targetClassName = "com/growingio/android/database/EventDataManager",
        targetMethodName = "removeEvents",
        targetMethodDesc = "(JILjava/lang/String;)I",
        injectClassName = "com/growingio/giokit/hook/GioDatabase",
        injectMethodName = "removeEvents",
        injectMethodDesc = "(JLjava/lang/String;)V",
    )
}