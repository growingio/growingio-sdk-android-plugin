package com.growingio.android.plugin.util

/**
 * <p>
 *
 * @author cpacm 2023/8/18
 */
interface ClassContextCompat {
    val className: String

    fun isAssignable(subClazz: String, superClazz: String): Boolean

    fun classIncluded(clazz: String): Boolean
}