/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

@file:Suppress("DEPRECATION")

package com.growingio.android.plugin.transform

import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.growingio.android.plugin.utils.AbstractKlassPool
import com.growingio.android.plugin.utils.info
import org.gradle.api.Project
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

/**
 * <p>
 *
 * @author cpacm 2022/3/31
 */
internal abstract class AutoTrackerBaseTransform(
    val project: Project,
    val android: BaseExtension,
) : Transform(), AutoTrackerTransformListener {

    // just for add android.jar to classloader
    private lateinit var androidKlassPool: AbstractKlassPool
    val bootKlassPool: AbstractKlassPool get() = androidKlassPool

    private val threadMxBean = ManagementFactory.getThreadMXBean()

    init {
        project.afterEvaluate {
            androidKlassPool = object : AbstractKlassPool(android.bootClasspath) {}
        }
    }

    override fun getName() = "baseAutotracker"

    override fun getInputTypes() = TransformManager.CONTENT_CLASS

    override fun getScopes() = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental() = true

    override fun transform(transformInvocation: TransformInvocation) {
        AutoTrackerClassRewriter(transformInvocation, this).apply {
            threadMxBean.sumCpuTime {
                if (isIncremental) {
                    doIncrementalTransform()
                } else {
                    outputProvider?.deleteAll()
                    doFullTransform()
                }
            }
        }
    }


    private fun <R> ThreadMXBean.sumCpuTime(action: () -> R): R {
        val ct0 = this.currentThreadCpuTime
        val result = action()
        val ct1 = this.currentThreadCpuTime
        info("[Growingio Transform]: ${(ct1 - ct0) / 1000000} ms")
        return result
    }
}