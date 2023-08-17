package com.growingio.android.plugin.transform

import java.io.File

interface AutoTrackerContext {
    val name: String

    /**
     * The project directory
     */
    val projectDir: File

    /**
     * The build directory
     */
    val buildDir: File

    /**
     * The boot classpath
     */
    val bootClasspath: Collection<File>

    /**
     * The compile classpath
     */
    val compileClasspath: Collection<File>

    /**
     * The runtime classpath
     */
    val runtimeClasspath: Collection<File>

    /**
     * The class pool
     */
    val klassPool: KlassPool

    /**
     * The application identifier
     */
    val applicationId: String

    /**
     * The buildType is debuggable
     */
    val isDebuggable: Boolean

    /**
     * Check if has the specified property. Generally, the property is equivalent to project property
     *
     * @param name the name of property
     */
    fun hasProperty(name: String): Boolean

    /**
     * Returns the value of the specified property. Generally, the property is equivalent to project property
     *
     * @param name the name of property
     * @param default the default value
     */
    fun <T> getProperty(name: String, default: T): T = default

}

interface AutoTrackerTransformListener {
    fun transform(context: AutoTrackerContext, bytecode: ByteArray): ByteArray
}

interface ClassContextCompat {
    val className: String

    fun isAssignable(subClazz: String, superClazz: String): Boolean

    fun classIncluded(clazz: String): Boolean
}

