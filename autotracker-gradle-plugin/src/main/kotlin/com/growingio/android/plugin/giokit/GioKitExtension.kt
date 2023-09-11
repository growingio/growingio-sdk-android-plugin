package com.growingio.android.plugin.giokit

import java.io.Serializable

/**
 * <p>
 *     Giokit plugin extension
 * @author cpacm 2023/9/5
 */
open class GioKitExtension : Serializable {

    var enabled = false

    //find the code that is manually buried
    var trackerFinderEnabled = true //disable if false

    // Analyze all buried point information under this domain value, default applicationId
    var trackerFinderDomain: Array<String>? = null
    var trackerCalledMethod: Array<String>? =
        null // user's custom method. eg:com.growingio.android.tracker#trackCumtomEvent

    var autoAttachEnabled = true
    var releaseEnabled = false
    var autoInstallVersion: String? = null
}

internal class GioKitParams(
    val enabled: Boolean,
    var xmlScheme: String = "",
    val dependLibs: String = "",
    val trackerFinderEnabled: Boolean = true,
    val trackerFinderDomain: Array<String> = arrayOf(),
    val trackerCalledMethod: Array<String> = arrayOf(),
    val autoAttachEnabled: Boolean = true
) : Serializable