@file:Suppress("DEPRECATION")

package com.growingio.android.plugin.transform

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.growingio.android.plugin.util.AndroidManifestHandler
import com.growingio.android.plugin.util.e
import com.growingio.android.plugin.util.g
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import org.gradle.api.Project
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import javax.xml.parsers.SAXParserFactory

inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T

@Suppress("UNCHECKED_CAST")
fun <T> Project.getProperty(name: String, defaultValue: T): T {
    val value = findProperty(name) ?: return defaultValue
    return when (defaultValue) {
        is Boolean -> if (value is Boolean) value as T else value.toString().toBoolean() as T
        is Byte -> if (value is Byte) value as T else value.toString().toByte() as T
        is Short -> if (value is Short) value as T else value.toString().toShort() as T
        is Int -> if (value is Int) value as T else value.toString().toInt() as T
        is Float -> if (value is Float) value as T else value.toString().toFloat() as T
        is Long -> if (value is Long) value as T else value.toString().toLong() as T
        is Double -> if (value is Double) value as T else value.toString().toDouble() as T
        is String -> if (value is String) value as T else value.toString() as T
        else -> value as T
    }
}

/**
 * Returns the corresponding variant of this transform invocation
 *
 */
fun TransformInvocation.getVariant(project: Project): BaseVariant? {
    val androidExtension = project.extensions.findByType(BaseExtension::class.java)
        ?: error("Android BaseExtension not found.")
    var variant: BaseVariant? = null
    when (androidExtension) {
        is AppExtension -> {
            androidExtension.applicationVariants.all {
                variant = it
            }
        }

        is LibraryExtension -> {
            androidExtension.libraryVariants.all { variant = it }
        }
    }
    return variant
}

fun getGioScheme(project: Project): String {
    var urlScheme = ""
    val appExtension = project.extensions.getByName("android") as? AppExtension
    appExtension?.let {
        val manifest = it.sourceSets.getAt("main").manifest.srcFile
        // 获取 AndroidManifest 下配置的 url scheme 如"growing.d80871b41ef40518"
        // 执行clean之后，无法再在 build 文件中找到 build/intermediates/merged_manifest/debug/AndroidManifest.xml，故无法找到 urlscheme
        // 第二次build时将会找到。。
        // 同样，如果只改变xml值未改变代码由于无法触发插件也不能实时更新
        manifest.let {
            val parser = SAXParserFactory.newInstance().newSAXParser()
            val handler = AndroidManifestHandler()
            parser.parse(manifest, handler)
            urlScheme = handler.growingioScheme ?: ""
            g("growingio scheme is ${urlScheme}")
            g("app package is ${handler.appPackageName}")
        }
    }
    return urlScheme
}

fun TransformInvocation.getBootClasspath(project: Project): Collection<File> {
    return project.getAndroid<BaseExtension>().bootClasspath
}

/**
 * Returns the compile classpath of this transform invocation
 *
 */
val TransformInvocation.compileClasspath: Collection<File>
    get() = listOf(inputs, referencedInputs).flatten().map {
        it.jarInputs + it.directoryInputs
    }.flatten().map {
        it.file
    }

/**
 * Returns the runtime classpath of this transform invocation
 */
fun TransformInvocation.getRuntimeClasspath(project: Project): Collection<File> {
    return compileClasspath + getBootClasspath(project)
}

fun File.transform(output: File, transformer: (ByteArray) -> ByteArray = { it -> it }) {
    when {
        isDirectory -> this.toURI().let { base ->
            this.search().parallelStream().forEach {
                it.transform(File(output, base.relativize(it.toURI()).path), transformer)
            }
        }

        isFile -> when (extension.toLowerCase(Locale.getDefault())) {
            "jar" -> JarFile(this).use {
                it.transform(output, transformer)
            }

            "class" -> this.inputStream().use {
                it.transform(transformer).redirect(output)
            }

            else -> this.copyTo(output, true)
        }

        else -> throw IOException("Unexpected file: ${this.canonicalPath}")
    }
}

fun InputStream.transform(transformer: (ByteArray) -> ByteArray): ByteArray {
    return transformer(readBytes())
}

fun ZipFile.transform(
    output: OutputStream,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    val entries = mutableSetOf<String>()
    val creator = ParallelScatterZipCreator(
        ThreadPoolExecutor(
            NCPU,
            NCPU,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            RejectedExecutionHandler { runnable, _ ->
                runnable.run()
            })
    )

    entries().asSequence().forEach { entry ->
        if (!entries.contains(entry.name)) {
            val zae = JarArchiveEntry(entry)
            val stream = InputStreamSupplier {
                when (entry.name.substringAfterLast('.', "")) {
                    "class" -> getInputStream(entry).use { src ->
                        try {
                            src.transform(transformer).inputStream()
                        } catch (e: Throwable) {
                            e("Broken class: ${this.name}!/${entry.name}")
                            getInputStream(entry)
                        }
                    }

                    else -> getInputStream(entry)
                }
            }

            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        } else {
            e("Duplicated jar entry: ${this.name}!/${entry.name}")
        }
    }

    ZipArchiveOutputStream(output).use(creator::writeTo)
}

fun ZipFile.transform(
    output: File,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) = output.touch().outputStream().buffered().use {
    transform(it, transformer)
}

fun ZipInputStream.transform(
    output: OutputStream,
    transformer: (ByteArray) -> ByteArray
) {
    val creator = ParallelScatterZipCreator()
    val entries = mutableSetOf<String>()

    while (true) {
        val entry = nextEntry?.takeIf { true } ?: break
        if (!entries.contains(entry.name)) {
            val zae = JarArchiveEntry(entry)
            val data = readBytes()
            val stream = InputStreamSupplier {
                transformer(data).inputStream()
            }
            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        }
    }

    ZipArchiveOutputStream(output).use(creator::writeTo)
}

fun ZipInputStream.transform(
    output: File,
    transformer: (ByteArray) -> ByteArray
) = output.touch().outputStream().buffered().use {
    transform(it, transformer)
}

private const val DEFAULT_BUFFER_SIZE = 8 * 1024

private fun InputStream.readBytes(estimatedSize: Int = DEFAULT_BUFFER_SIZE): ByteArray {
    val buffer = ByteArrayOutputStream(estimatedSize.coerceAtLeast(this.available()))
    copyTo(buffer)
    return buffer.toByteArray()
}

private fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
    }
    return bytesCopied
}
