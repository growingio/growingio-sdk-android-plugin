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

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.growingio.android.plugin.AutoTrackerParams
import com.growingio.android.plugin.giokit.GioKitCodeVisitor
import com.growingio.android.plugin.giokit.GioKitInjectData
import com.growingio.android.plugin.giokit.GioKitInjectVisitor
import com.growingio.android.plugin.giokit.GioKitParams
import com.growingio.android.plugin.giokit.GioKitProcessor
import com.growingio.android.plugin.util.ClassContextCompat
import com.growingio.android.plugin.util.EXECUTE_INJECT_CLASS
import com.growingio.android.plugin.util.normalize
import com.growingio.android.plugin.util.shouldClassModified
import com.growingio.android.plugin.util.w
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2022/4/6
 */
internal abstract class AutoTrackerFactory :
    AsmClassVisitorFactory<AutoTrackerParameters> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        val classContextCompat = object : ClassContextCompat {
            override val className = classContext.currentClassData.className

            override fun isAssignable(subClazz: String, superClazz: String): Boolean {
                return classContext.loadClassData(normalize(subClazz))?.let {
                    it.className == normalize(superClazz) || it.superClasses.indexOf(normalize(superClazz)) >= 0 || it.interfaces.indexOf(
                        normalize(superClazz)
                    ) >= 0
                } ?: false
            }

            override fun classIncluded(clazz: String): Boolean {
                val included = EXECUTE_INJECT_CLASS.contains(normalize(clazz))
                if (!included) {
                    w("$clazz not included in default inject class")
                    return false
                }
                if (GioKitInjectData.GIOKIT_INJECT_CLASS.contains(normalize(clazz))) {
                    val gioKitParams = parameters.get().gioKitParams.get()
                    return gioKitParams.enabled
                }
                // classContext.loadClassData(normalize(clazz)) ?: return false
                return true
            }
        }
        val apiVersion = instrumentationContext.apiVersion.get()

        val gioKitParams = parameters.get().gioKitParams.get()
        var classVisitor = nextClassVisitor
        if (gioKitParams.enabled) {
            if (gioKitParams.trackerFinderEnabled) {
                val applicationId = parameters.get().applicationId.get()
                val domains = GioKitProcessor.getDefaultFindDomains(gioKitParams.trackerFinderDomain, applicationId)
                domains.firstOrNull {
                    normalize(classContext.currentClassData.className).startsWith(it)
                }?.apply {
                    classVisitor = GioKitCodeVisitor(
                        apiVersion, classVisitor, classContextCompat,
                        GioKitProcessor.getDefaultCalledMethods(gioKitParams.trackerCalledMethod),
                        GioKitProcessor.getGeneratedDir(parameters.get().buildDir.get(), parameters.get().name.get()),
                        GioKitProcessor.getVisitorCodeFile(parameters.get().buildDir.get()),
                    )
                }
            }

            if (GioKitProcessor.shouldClassModified(classContext.currentClassData.className)) {
                classVisitor = GioKitInjectVisitor(apiVersion, classVisitor, classContextCompat, gioKitParams)
            }
        }

        return DesugarClassVisitor(
            apiVersion,
            InjectTargetClassVisitor(
                apiVersion,
                InjectAroundClassVisitor(
                    apiVersion,
                    InjectSuperClassVisitor(apiVersion, classVisitor, classContextCompat),
                    classContextCompat
                ), classContextCompat
            ),
            classContextCompat
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val autoTrackerParams = parameters.get().autoTrackerParams.get()
        val shouldVisit = shouldClassModified(
            autoTrackerParams.excludePackages,
            autoTrackerParams.includePackages,
            classData.className
        )
        if (!shouldVisit) {
            val gioKitParams = parameters.get().gioKitParams.get()
            if (!gioKitParams.enabled) return shouldVisit
            return GioKitProcessor.shouldClassModified(classData.className)
        }
        return shouldVisit
    }
}


internal interface AutoTrackerParameters : InstrumentationParameters {

    /**
     * AGP will re-instrument dependencies, when the [InstrumentationParameters] changed
     * https://issuetracker.google.com/issues/190082518#comment4. This is just a dummy parameter
     * that is used solely for that purpose.
     */
    @get:Input
    val name: Property<String>

    @get:Input
    val applicationId: Property<String>

    @get:Input
    val buildDir: Property<File>

    @get:Input
    val autoTrackerParams: Property<AutoTrackerParams>

    @get:Input
    val gioKitParams: Property<GioKitParams>

}
