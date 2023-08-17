package com.growingio.android.plugin.util

import org.gradle.api.Project

fun getAndroidComponentsExtension(project: Project): AndroidComponentsExtensionCompat {
    val version = SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION
    return when {
        version >= SimpleAGPVersion(8, 1) -> {
            AndroidComponentsExtensionCompatApi81Impl(project)
        }
        version >= SimpleAGPVersion(7, 2) -> {
            AndroidComponentsExtensionCompatApi72Impl(project)
        }
        version >= SimpleAGPVersion(7, 1) -> {
            AndroidComponentsExtensionCompatApi71Impl(project)
        }
        version >= SimpleAGPVersion(7, 0) -> {
            AndroidComponentsExtensionCompatApi70Impl(project)
        }
        version >= SimpleAGPVersion(4, 2) -> {
            AndroidComponentsExtensionCompatApi42Impl(project)
        }
        else -> {
            error("Android Gradle Plugin $version is not supported")
        }
    }
}