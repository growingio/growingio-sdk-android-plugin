package com.growingio.android.plugin.transform

import com.growingio.android.plugin.util.asIterable
import java.io.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.RecursiveTask

/**
 * Find files from the specified paths
 */
class FileSearch internal constructor(
    private val roots: Iterable<File>,
    private val filter: (File) -> Boolean = { true }
) : RecursiveTask<Collection<File>>() {

    internal constructor(roots: Array<File>, filter: (File) -> Boolean = { true }) : this(roots.toList(), filter)

    internal constructor(root: File, filter: (File) -> Boolean = { true }) : this(listOf(root), filter)

    override fun compute(): Collection<File> {
        val tasks = mutableListOf<RecursiveTask<Collection<File>>>()
        val result = mutableSetOf<File>()

        roots.forEach { root ->
            if (root.isDirectory) {
                root.listFiles()?.let { files ->
                    FileSearch(files, filter).also { task ->
                        tasks.add(task)
                    }.fork()
                }
            } else if (root.isFile) {
                if (filter.invoke(root)) {
                    result.add(root)
                }
            }
        }

        return result + tasks.flatMap { it.join() }
    }

}

@JvmOverloads
fun File.search(filter: (File) -> Boolean = { true }): Collection<File> = FileSearch(this, filter).execute()

@JvmOverloads
fun Iterable<File>.search(filter: (File) -> Boolean = { true }): Collection<File> = FileSearch(this, filter).execute()

@JvmOverloads
fun Iterator<File>.search(filter: (File) -> Boolean = { true }): Collection<File> =
    FileSearch(this.asIterable(), filter).execute()

@JvmOverloads
fun Array<File>.search(filter: (File) -> Boolean = { true }): Collection<File> = FileSearch(this, filter).execute()

fun <T> ForkJoinTask<T>.execute(): T {
    val pool = ForkJoinPool()
    val result = pool.invoke(this)
    pool.shutdown()
    return result
}

fun File.file(vararg path: String) = File(this, path.joinToString(File.separator))

/**
 * Create a new file if not exists
 *
 */
fun File.touch(): File {
    if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
    return this
}

/**
 * Return the first line of file
 */
fun File.head(): String? = inputStream().use { it.head() }

/**
 * Returns the first line of input stream
 */
fun InputStream.head(): String? = BufferedReader(InputStreamReader(this)).head()

/**
 * Returns the first line of reader
 */
fun Reader.head(): String? = BufferedReader(this).readLine()

/**
 * Redirect this input stream to the specified file
 *
 * @author johnsonlee
 */
fun InputStream.redirect(file: File): Long = file.touch().outputStream().use { this.copyTo(it) }

/**
 * Redirect this byte data to the specified file
 *
 * @author johnsonlee
 */
fun ByteArray.redirect(file: File): Long = this.inputStream().use { it.redirect(file) }

/**
 * Redirect this byte data to the specified output stream
 *
 * @author johnsonlee
 */
fun ByteArray.redirect(output: OutputStream): Long = this.inputStream().copyTo(output)


val NCPU = Runtime.getRuntime().availableProcessors()