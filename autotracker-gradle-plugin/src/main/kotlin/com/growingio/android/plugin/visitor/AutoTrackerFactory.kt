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
import com.growingio.android.plugin.utils.DEFAULT_INJECT_CLASS
import com.growingio.android.plugin.utils.normalize
import com.growingio.android.plugin.utils.shouldClassModified
import org.gradle.api.tasks.Internal
import org.gradle.api.provider.Property
import org.objectweb.asm.ClassVisitor
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2022/4/6
 */
internal abstract class AutoTrackerFactory :
    AsmClassVisitorFactory<AutoTrackerParams> {

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
                return DEFAULT_INJECT_CLASS.contains(normalize(clazz))
            }
        }
        val apiVersion = instrumentationContext.apiVersion.get()

        return DesugarClassVisitor(
            apiVersion,
            InjectTargetClassVisitor(
                apiVersion,
                InjectAroundClassVisitor(
                    apiVersion,
                    InjectSuperClassVisitor(apiVersion, nextClassVisitor, classContextCompat),
                    classContextCompat
                ), classContextCompat
            ),
            classContextCompat
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return shouldClassModified(
            parameters.get().excludePackages.get(),
            parameters.get().includePackages.get(),
            classData.className
        )
    }
}


internal interface AutoTrackerParams : InstrumentationParameters {
    @get:Internal
    val additionalClassesDir: Property<File>

    @get:Internal
    val excludePackages: Property<Array<String>>

    @get:Internal
    val includePackages: Property<Array<String>>

}


interface ClassContextCompat {
    val className: String

    fun isAssignable(subClazz: String, superClazz: String): Boolean

    fun classIncluded(clazz: String): Boolean
}