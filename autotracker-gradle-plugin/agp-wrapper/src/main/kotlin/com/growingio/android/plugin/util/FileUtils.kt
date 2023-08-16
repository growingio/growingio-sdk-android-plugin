package com.growingio.android.plugin.util

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

