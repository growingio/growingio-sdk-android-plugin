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
import com.growingio.android.plugin.AutoTrackerExtension
import com.growingio.android.plugin.giokit.GioKitCodeVisitor
import com.growingio.android.plugin.giokit.GioKitExtension
import com.growingio.android.plugin.giokit.GioKitInjectData
import com.growingio.android.plugin.giokit.GioKitInjectVisitor
import com.growingio.android.plugin.giokit.GioKitParams
import com.growingio.android.plugin.giokit.GioKitProcessor
import com.growingio.android.plugin.transform.AutoTrackerContext
import com.growingio.android.plugin.transform.GrowingBaseTransform
import com.growingio.android.plugin.util.ClassContextCompat
import com.growingio.android.plugin.util.normalize
import com.growingio.android.plugin.util.w
import com.growingio.android.plugin.util.*
import com.growingio.android.plugin.util.shouldClassModified
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * <p>
 *
 * @author cpacm 2023/9/11
 */
internal class AutoTrackerTransform(
    project: Project, android: BaseExtension,
    private val gioExtension: AutoTrackerExtension
) : GrowingBaseTransform(project, android) {

    override fun getName() = "AutoTrackerTransform"
    private val giokitParams: GioKitParams

    init {
        giokitParams = if (GioKitProcessor.checkGiokitEnabled(project, gioExtension)) {
            val scheme = ""
            val dependLibs = GioKitProcessor.getGioDepends(project)
            val gioKitExtension = gioExtension.giokit ?: GioKitExtension()
            val trackerFinderEnabled = gioKitExtension.trackerFinderEnabled
            val trackerFinderDomain: Array<String> = gioKitExtension.trackerFinderDomain ?: arrayOf()
            val trackerCalledMethod: Array<String> = gioKitExtension.trackerCalledMethod ?: arrayOf()
            val autoAttachEnabled: Boolean = gioKitExtension.autoAttachEnabled
            GioKitParams(
                true,
                scheme,
                dependLibs,
                trackerFinderEnabled,
                trackerFinderDomain,
                trackerCalledMethod,
                autoAttachEnabled,
            )
        } else {
            GioKitParams(false)
        }
    }

    override fun transform(context: AutoTrackerContext, bytecode: ByteArray): ByteArray {
        var className: String = context.name
        try {
            val classReader = ClassReader(bytecode)
            className = classReader.className

            val shouldVisit = shouldClassModified(
                gioExtension.excludePackages ?: arrayOf(),
                gioExtension.includePackages ?: arrayOf(),
                normalize(className)
            )
            if (!shouldVisit) {
                if (!giokitParams.enabled) return bytecode
                if (!GioKitProcessor.shouldClassModified(normalize(className))) {
                    return bytecode
                }
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
                    val included = EXECUTE_INJECT_CLASS.contains(normalize(clazz))
                    if (!included) {
                        w("$clazz not included in default inject class")
                    }

                    if (GioKitInjectData.GIOKIT_INJECT_CLASS.contains(normalize(clazz))) {
                        return giokitParams.enabled
                    }

                    return included
                }
            }

            var classVisitor: ClassVisitor = autoTrackerWriter
            if (giokitParams.enabled) {
                if (giokitParams.trackerFinderEnabled) {
                    val applicationId = context.applicationId
                    val domains = GioKitProcessor.getDefaultFindDomains(giokitParams.trackerFinderDomain, applicationId)
                    domains.firstOrNull {
                        normalize(className).startsWith(it)
                    }?.apply {
                        classVisitor = GioKitCodeVisitor(
                            apiVersion, classVisitor, classContextCompat,
                            GioKitProcessor.getDefaultCalledMethods(giokitParams.trackerCalledMethod),
                            GioKitProcessor.getGeneratedDir(context.buildDir, context.name),
                            GioKitProcessor.getVisitorCodeFile(context.buildDir),
                        )
                    }

                    val sourcePath = GioKitProcessor.getGeneratedDir(context.buildDir, context.name)
                    android.sourceSets.forEach {sourceSet->
                        if (sourceSet.name == "main") {
                            sourceSet.java.srcDirs.plus(sourcePath)
                        }
                    }
                }

                if (GioKitProcessor.shouldClassModified(normalize(className))) {
                    giokitParams.xmlScheme = context.gioScheme
                    classVisitor = GioKitInjectVisitor(apiVersion, classVisitor, classContextCompat, giokitParams)
                }
            }


            val visitor = DesugarClassVisitor(
                apiVersion,
                InjectTargetClassVisitor(
                    apiVersion, InjectAroundClassVisitor(
                        apiVersion,
                        InjectSuperClassVisitor(apiVersion, classVisitor, classContextCompat),
                        classContextCompat
                    ), classContextCompat
                ), classContextCompat
            )
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES)
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