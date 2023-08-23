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

package com.growingio.android.plugin.visitor

import com.android.build.gradle.BaseExtension
import com.growingio.android.plugin.SaasAutoTrackerExtension
import com.growingio.android.plugin.transform.AutoTrackerContext
import com.growingio.android.plugin.util.ClassContextCompat
import com.growingio.android.plugin.transform.GrowingBaseTransform
import com.growingio.android.plugin.util.normalize
import com.growingio.android.plugin.util.w
import com.growingio.android.plugin.util.*
import com.growingio.android.plugin.util.shouldClassModified
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * <p>
 *
 * @author cpacm 2022/4/2
 */
internal class SaasAutoTrackerTransform(
    project: Project, android: BaseExtension,
    private val gioExtension: SaasAutoTrackerExtension
) : GrowingBaseTransform(project, android) {

    override fun getName() = "SaasAutoTrackerTransform"

    override fun transform(context: AutoTrackerContext, bytecode: ByteArray): ByteArray {
        var className: String = context.name
        try {
            val classReader = ClassReader(bytecode)
            className = classReader.className
            if (!shouldClassModified(
                    gioExtension.excludePackages ?: arrayOf(),
                    gioExtension.includePackages ?: arrayOf(),
                    normalize(classReader.className)
                )
            ) {
                return bytecode
            }


            val autoTrackerWriter = object : ClassWriter(classReader, COMPUTE_MAXS) {
                fun getApi(): Int {
                    return api
                }
            }
            val apiVersion = autoTrackerWriter.getApi()

            val classContextCompat = object : ClassContextCompat {
                override val className = classReader.className
                override fun isAssignable(subClazz: String, superClazz: String): Boolean {
                    return context.klassPool.get(superClazz).isAssignableFrom(subClazz)
                }

                override fun classIncluded(clazz: String): Boolean {
                    val included = DEFAULT_INJECT_CLASS.contains(normalize(clazz))
                    if (!included) {
                        w("$clazz not included in default inject class")
                    }
                    return included
                }
            }

            val visitor = DesugarClassVisitor(
                apiVersion,
                InjectTargetClassVisitor(
                    apiVersion, InjectAroundClassVisitor(
                        apiVersion,
                        InjectSuperClassVisitor(apiVersion, autoTrackerWriter, classContextCompat),
                        classContextCompat
                    ), classContextCompat
                ), classContextCompat
            )

            val saasVisitor = SaasConfigClassVisitor(apiVersion, visitor, classContextCompat)

            classReader.accept(saasVisitor, ClassReader.EXPAND_FRAMES)
            return autoTrackerWriter.toByteArray()
        } catch (t: Throwable) {
            w(
                "Unfortunately, an error has occurred while processing $className. Please check this class and decide whether to ignore it in plugin setting:「growingAutotracker -> excludePackages」. Or copy your build logs and the jar containing this class and visit https://www.growingio.com, thanks!\n",
                t
            )
        }
        return bytecode
    }
}