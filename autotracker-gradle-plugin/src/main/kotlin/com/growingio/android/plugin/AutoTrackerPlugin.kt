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


package com.growingio.android.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.growingio.android.plugin.giokit.GioKitProcessor
import com.growingio.android.plugin.visitor.AutoTrackerTransform
import com.growingio.android.plugin.util.ComponentCompat
import com.growingio.android.plugin.util.LOG_ENABLE
import com.growingio.android.plugin.util.SimpleAGPVersion
import com.growingio.android.plugin.util.getAndroidComponentsExtension
import com.growingio.android.plugin.util.initInjectClass
import com.growingio.android.plugin.util.w
import com.growingio.android.plugin.visitor.AutoTrackerFactory
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */
class AutoTrackerPlugin @Inject constructor(val instantiator: Instantiator) : Plugin<Project> {

    override fun apply(project: Project) {

        var inAndroidProject = false

        val autoTrackerExtension =
            project.extensions.create("growingAutotracker", AutoTrackerExtension::class.java, instantiator)

        project.plugins.withType(AndroidBasePlugin::class.java) {
            inAndroidProject = true
            if (SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION < SimpleAGPVersion(4, 2)) {
                // Configures bytecode transform using older APIs pre AGP 4.2
                configureBytecodeTransform(project, autoTrackerExtension)
            } else {
                // Configures bytecode transform using AGP 4.2 ASM pipeline.
                configureBytecodeTransformASM(project, autoTrackerExtension)
            }
        }

        project.afterEvaluate {
            checkJavaVersion()
            check(inAndroidProject) {
                "The GrowingIO AutoTracker Gradle plugin can only be applied to an Android project"
            }
            LOG_ENABLE = autoTrackerExtension.logEnabled
            initInjectClass(autoTrackerExtension)
            checkAutoTrackerDependency(it, autoTrackerExtension.skipDependencyCheck)
        }
    }

    private fun configureBytecodeTransformASM(project: Project, gioExtension: AutoTrackerExtension) {
        fun registerTransform(androidComponent: ComponentCompat) {
            val variant = androidComponent.getComponentVariant() as? ApplicationVariant
            val applicationId = variant?.applicationId?.get() ?: ""
            val buildDir = project.layout.buildDirectory.get().asFile
            val autoTrackerParams = AutoTrackerParams(
                gioExtension.excludePackages ?: arrayOf(),
                gioExtension.includePackages ?: arrayOf(),
                gioExtension.injectClasses ?: arrayOf(),
                gioExtension.analyticsAdapter ?: AnalyticsAdapter()
            )

            val gioKitParams = GioKitProcessor.processGiokitParams(project, gioExtension)
            if (gioExtension.giokit?.enabled == true) {
                GioKitProcessor.autoInstallGioKit(
                    project,
                    gioExtension.giokit?.releaseEnabled ?: false,
                    gioExtension.giokit?.autoInstallVersion
                )
                if (gioExtension.giokit?.trackerFinderEnabled == true) {
                    val sourcePath = GioKitProcessor.getGeneratedDir(buildDir, androidComponent.name)
                    GioKitProcessor.createGiokitSourceSets(project, sourcePath.absolutePath)
                }
            }

            androidComponent.transformClassesWith(
                classVisitorFactoryImplClass = AutoTrackerFactory::class.java,
                scope = InstrumentationScope.ALL
            ) { params ->
                //val classesDir = File(project.buildDir, "intermediates/javac/${androidComponent.name}/classes")
                //params.additionalClassesDir.set(classesDir)
                params.name.set(androidComponent.name)
                params.applicationId.set(applicationId)
                params.buildDir.set(buildDir)
                params.autoTrackerParams.set(autoTrackerParams)
                params.gioKitParams.set(gioKitParams)
            }
            androidComponent.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
        getAndroidComponentsExtension(project).onAllVariants({ registerTransform(it) }, {})
    }

    private fun configureBytecodeTransform(project: Project, gioExtension: AutoTrackerExtension) {
        val androidExtension =
            project.extensions.findByType(BaseExtension::class.java) ?: error("Android BaseExtension not found.")

        if (gioExtension.giokit?.enabled == true) {
            GioKitProcessor.autoInstallGioKit(
                project,
                gioExtension.giokit?.releaseEnabled ?: false,
                gioExtension.giokit?.autoInstallVersion
            )
        }

        androidExtension::class.java.getMethod(
            "registerTransform",
            Class.forName("com.android.build.api.transform.Transform"),
            Array<Any>::class.java
        ).invoke(androidExtension, AutoTrackerTransform(project, androidExtension, gioExtension), emptyArray<Any>())
    }

    private fun checkJavaVersion() {
        if (SimpleAGPVersion.ANDROID_GRADLE_PLUGIN_VERSION >= SimpleAGPVersion(7, 0)) {
            if (JavaVersion.current() < JavaVersion.VERSION_11) {
                error("GrowingIO AutoTracker Gradle Plugin request at least Java 11")
            }
        } else if (JavaVersion.current() < JavaVersion.VERSION_1_8) {
            error("GrowingIO AutoTracker Gradle Plugin request at least Java 8")
        }
    }

    /**
     * allow plugin version different with dependency version
     */
    private fun checkAutoTrackerDependency(project: Project, skip: Boolean = false) {
        if (project.state.failure != null || skip) {
            return
        }

        val dependencies = project.configurations.flatMap { configuration ->
            configuration.dependencies.map { dependency ->
                //info(dependency.toString())
                dependency.group to dependency.name
            }
        }

        if (dependencies.contains(LIBRARY_GROUP to "autotracker-core")) {
            w("The Growingio autotracker core sdk dependency was found And you need custom owns sdk.")
        } else if (dependencies.contains(LIBRARY_GROUP to "autotracker-cdp")) {
            w("The Growingio autotracker-cdp was deprecated.")
        } else if (!dependencies.contains(LIBRARY_GROUP to "autotracker")) {
            error("The GrowingIO Gradle plugin is applied but no growingio autotracker sdk dependency was found. Or you have added autotracker dependency and skip dependency check through set <skipDependencyCheck true> in build.gradle extension")
        }

    }

    companion object {
        const val LIBRARY_GROUP = "com.growingio.android"
    }
}