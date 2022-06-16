/*
*   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*        http:..www.apache.org.licenses.LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.growingio.android.plugin.transform

import com.android.build.api.transform.*
import com.growingio.android.plugin.utils.AbstractKlassPool
import com.growingio.android.plugin.utils.NCPU
import com.growingio.android.plugin.utils.e
import com.growingio.android.plugin.utils.info
import java.io.File
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/*
*
* Transform agent
* @author cpacm 2022.4.1
*/
@Suppress("DEPRECATION")
internal class GrowingClassRewriter(
    private val delegate: TransformInvocation,
    internal val transform: GrowingBaseTransform,
) : TransformInvocation by delegate, AutoTrackerContext {

    private val project = transform.project

    override val name: String = delegate.context.variantName

    override val projectDir: File = project.projectDir

    override val buildDir: File = project.buildDir

    override val bootClasspath = delegate.getBootClasspath(project)

    override val compileClasspath = delegate.compileClasspath

    override val runtimeClasspath = delegate.getRuntimeClasspath(project)

    override val klassPool: AbstractKlassPool =
        object : AbstractKlassPool(compileClasspath, transform.bootKlassPool) {}

    override val applicationId = getVariant(project)?.applicationId ?: "unknown"

    override val isDebuggable = getVariant(project)?.buildType?.isDebuggable ?: false

    override fun hasProperty(name: String) = project.hasProperty(name)

    override fun <T> getProperty(name: String, default: T): T = project.getProperty(name, default)

    internal fun doFullTransform() {
        this.outputProvider.deleteAll()
        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            this.inputs.map {
                it.jarInputs + it.directoryInputs
            }.flatten().map { input ->
                executor.submit {
                    val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
                    outputProvider?.let { provider ->
                        info("Transforming ${input.file}")
                        input.transform(
                            provider.getContentLocation(
                                input.name,
                                input.contentTypes,
                                input.scopes,
                                format
                            )
                        )
                    }
                }
            }.forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }
    }

    internal fun doIncrementalTransform() {
        val workerExecutor = Executors.newFixedThreadPool(NCPU)
        try {
            this.inputs.map { input ->
                input.jarInputs.filter { it.status != Status.NOTCHANGED }.map { jarInput ->
                    workerExecutor.submit {
                        doIncrementalTransform(jarInput)
                    }
                } + input.directoryInputs.filter { it.changedFiles.isNotEmpty() }.map { dirInput ->
                    val base = dirInput.file.toURI()
                    workerExecutor.submit {
                        doIncrementalTransform(dirInput, base)
                    }
                }
            }.flatten().forEach {
                it.get()
            }
        } finally {
            workerExecutor.shutdown()
            workerExecutor.awaitTermination(1, TimeUnit.HOURS)
        }
    }

    private fun doIncrementalTransform(jarInput: JarInput) {
        when (jarInput.status) {
            Status.REMOVED -> jarInput.file.delete()
            Status.CHANGED, Status.ADDED -> {
                info("Transforming ${jarInput.file}")
                outputProvider?.let { provider ->
                    jarInput.transform(
                        provider.getContentLocation(
                            jarInput.name,
                            jarInput.contentTypes,
                            jarInput.scopes,
                            Format.JAR
                        )
                    )
                }
            }
            else -> {}
        }
    }

    private fun doIncrementalTransform(dirInput: DirectoryInput, base: URI) {
        dirInput.changedFiles.forEach { (file, status) ->
            when (status) {
                Status.REMOVED -> {
                    info("Deleting $file")
                    outputProvider?.let { provider ->
                        provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        ).parentFile.listFiles()?.asSequence()
                            ?.filter { it.isDirectory }
                            ?.map { File(it, dirInput.file.toURI().relativize(file.toURI()).path) }
                            ?.filter { it.exists() }
                            ?.forEach { it.delete() }
                    }
                    file.delete()
                }
                Status.ADDED, Status.CHANGED -> {
                    info("Transforming $file")
                    outputProvider?.let { provider ->
                        val root = provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        )
                        val output = File(root, base.relativize(file.toURI()).path)
                        file.transform(output) { bytecode ->
                            bytecode.transform()
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun QualifiedContent.transform(output: File) {
        try {
            this.file.transform(output) { bytecode ->
                bytecode.transform()
            }
        } catch (e: Exception) {
            e("e==>${e.message}", e)
        }
    }

    private fun ByteArray.transform(): ByteArray {
        return transform.transform(this@GrowingClassRewriter, this)
    }
}