package com.growingio.android.plugin.util

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

fun <T> Iterator<T>.asIterable(): Iterable<T> = Iterable { this }

fun String.simpleClass(): String {
    return this.split("/").last()
}

fun String.unNormalize(): String {
    return if (this.contains('.')) {
        this.replace('.', '/')
    } else {
        this
    }
}

fun normalize(type: String) = if (type.contains('/')) {
    type.replace('/', '.')
} else {
    type
}

fun isAndroidGenerated(className: String): Boolean {
    return className.contains("R$") ||
            className.contains("R2$") ||
            className.contains("R.class") ||
            className.contains("R2.class") ||
            className.contains("BuildConfig.class")
}

class AndroidManifestHandler : DefaultHandler() {

    companion object {
        const val ATTR_NAME = "android:name"
        const val MANIFEST_ATTR_NAME = "package"
    }

    var growingioScheme: String? = null
    var appPackageName: String? = null

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        // val name: String = attributes?.getValue(ATTR_NAME) ?: ""
        val packageName: String = attributes?.getValue(MANIFEST_ATTR_NAME) ?: ""
        when (qName) {
            "manifest" -> appPackageName = packageName
            "application" -> {} //applications.add(name)
            "activity" -> {} //activities.add(name)
            "service" -> {} //services.add(name)
            "provider" -> {} //providers.add(name)
            "receiver" -> {} //receivers.add(name)
            "data" -> {
                attributes?.getValue("android:scheme")?.let {
                    if (it.startsWith("growing")) {
                        growingioScheme = it
                    }
                }
            }

        }
    }
}

