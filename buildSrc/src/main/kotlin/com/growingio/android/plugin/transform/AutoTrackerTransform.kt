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

package com.growingio.android.plugin.transform

import com.android.build.gradle.BaseExtension
import com.growingio.android.plugin.AutoTrackerExtension
import com.growingio.android.plugin.utils.e
import com.growingio.android.plugin.utils.info
import com.growingio.android.plugin.utils.shouldClassModified
import com.growingio.android.plugin.visitor.ClassContextCompat
import com.growingio.android.plugin.visitor.DesugarClassVisitor
import com.growingio.android.plugin.visitor.InjectAroundClassVisitor
import com.growingio.android.plugin.visitor.InjectSuperClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * <p>
 *
 * @author cpacm 2022/4/2
 */
internal class AutoTrackerTransform(
    project: Project, android: BaseExtension,
    private val gioExtension: AutoTrackerExtension
) : AutoTrackerBaseTransform(project, android) {

    override fun getName() = "AutoTrackerTransform"

    override fun transform(context: AutoTrackerContext, bytecode: ByteArray): ByteArray {
        if (!shouldClassModified(
                gioExtension.excludePackages ?: arrayOf(),
                gioExtension.includePackages ?: arrayOf(),
                context.name
            )
        ) {
            return bytecode
        }

        val classContextCompat = object : ClassContextCompat {
            override val className = context.name
            override fun isAssignable(subClazz: String, superClazz: String): Boolean {
                return context.klassPool.get(superClazz).isAssignableFrom(subClazz)
            }
        }

        try {
            val classReader = ClassReader(bytecode)
            val autoTrackerWriter = object : ClassWriter(classReader, COMPUTE_MAXS) {
                fun getApi(): Int {
                    return api
                }
            }
            val apiVersion = autoTrackerWriter.getApi()
            val visitor = DesugarClassVisitor(
                apiVersion, InjectAroundClassVisitor(
                    apiVersion,
                    InjectSuperClassVisitor(apiVersion, autoTrackerWriter, classContextCompat),
                    classContextCompat
                ), classContextCompat
            )
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
            return autoTrackerWriter.toByteArray()
        } catch (t: Throwable) {
            e(
                "Unfortunately, an error has occurred while processing " + context.name + ". Please copy your build logs and the jar containing this class and visit https://www.growingio.com, thanks!\n",
                t
            )
        }
        return bytecode
    }
}