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

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.android.build.api.transform.QualifiedContent.Scope
import com.growingio.android.plugin.utils.AbstractKlassPool
import com.growingio.android.plugin.utils.info
import org.gradle.api.Project
import org.jetbrains.kotlin.com.google.common.collect.ImmutableSet
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

/**
 * <p>
 *
 * @author cpacm 2022/3/31
 */
abstract class GrowingBaseTransform(
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

    override fun getName() = "GioAutotracker"

    override fun getInputTypes() = ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)

    override fun getScopes() = ImmutableSet.of(Scope.PROJECT, Scope.SUB_PROJECTS, Scope.EXTERNAL_LIBRARIES)

    override fun isIncremental() = true

    override fun transform(transformInvocation: TransformInvocation) {
        GrowingClassRewriter(transformInvocation, this).apply {
            threadMxBean.sumCpuTime {
                if (isIncremental) {
                    doIncrementalTransform()
                } else {
                    doFullTransform()
                }
            }
        }
    }


    private fun <R> ThreadMXBean.sumCpuTime(action: () -> R): R {
        val ct0 = this.currentThreadCpuTime
        val result = action()
        val ct1 = this.currentThreadCpuTime
        info("[GrowingIO Transform]: ${(ct1 - ct0) / 1000000} ms")
        return result
    }
}