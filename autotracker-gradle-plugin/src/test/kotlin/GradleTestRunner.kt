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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2022/4/29
 */
class GradleTestRunner(val tempFolder: TemporaryFolder) {
    private val dependencies = mutableListOf<String>()
    private val activities = mutableListOf<String>()
    private val additionalAndroidOptions = mutableListOf<String>()
    private val gioOptions = mutableListOf<String>()
    private var appClassName: String? = null
    private var buildFile: File? = null
    private var gradlePropertiesFile: File? = null
    private var localPropertiesFile: File? = null
    private var manifestFile: File? = null
    private var additionalTasks = mutableListOf<String>()

    init {
        tempFolder.newFolder("src", "main", "java", "growingio")
        tempFolder.newFolder("src", "test", "java", "growingio")
        tempFolder.newFolder("src", "main", "res")
        addDependencies("implementation 'com.growingio.android:autotracker-cdp:3.4.1'")
    }

    // Adds project dependencies, e.g. "implementation <group>:<id>:<version>"
    fun addDependencies(vararg deps: String) {
        dependencies.addAll(deps)
    }

    fun clearDependencies() {
        dependencies.clear()
    }

    // Adds an <activity> tag in the project's Android Manifest, e.g. "<activity name=".Foo"/>
    fun addActivities(vararg activityElements: String) {
        activities.addAll(activityElements)
    }

    // Adds 'android' options to the project's build.gradle, e.g. "lintOptions.checkReleaseBuilds = false"
    fun addAndroidOption(vararg options: String) {
        additionalAndroidOptions.addAll(options)
    }

    // Adds 'gio' options to the project's build.gradle, e.g. "includePackages "com.xxxx""
    fun addGioOption(vararg options: String) {
        gioOptions.addAll(options)
    }

    // Adds a source package to the project. The package path is relative to 'src/main/java'.
    fun addSrcPackage(packagePath: String) {
        File(tempFolder.root, "src/main/java/$packagePath").mkdirs()
    }

    // Adds a source file to the project. The source path is relative to 'src/main/java'.
    fun addSrc(srcPath: String, srcContent: String): File {
        File(tempFolder.root, "src/main/java/${srcPath.substringBeforeLast(File.separator)}").mkdirs()
        return tempFolder.newFile("/src/main/java/$srcPath").apply { writeText(srcContent) }
    }

    // Adds a test source file to the project. The source path is relative to 'src/test/java'.
    fun addTestSrc(srcPath: String, srcContent: String): File {
        File(tempFolder.root, "src/test/java/${srcPath.substringBeforeLast(File.separator)}").mkdirs()
        return tempFolder.newFile("/src/test/java/$srcPath").apply { writeText(srcContent) }
    }

    // Adds a resource file to the project. The source path is relative to 'src/main/res'.
    fun addRes(resPath: String, resContent: String): File {
        File(tempFolder.root, "src/main/res/${resPath.substringBeforeLast(File.separator)}").mkdirs()
        return tempFolder.newFile("/src/main/res/$resPath").apply { writeText(resContent) }
    }

    fun setAppClassName(name: String) {
        appClassName = name
    }

    fun runAdditionalTasks(taskName: String) {
        additionalTasks.add(taskName)
    }

    // Executes a Gradle builds and expects it to succeed.
    fun build(): Result {
        setupFiles()
        return Result(tempFolder.root, createRunner().build())
    }

    // Executes a Gradle build and expects it to fail.
    fun buildAndFail(): Result {
        setupFiles()
        return Result(tempFolder.root, createRunner().buildAndFail())
    }

    private fun setupFiles() {
        writeBuildFile()
        writeGradleProperties()
        writeLocalProperties()
        writeAndroidManifest()
    }

    private fun writeBuildFile() {
        buildFile?.delete()
        buildFile = tempFolder.newFile("build.gradle").apply {
            writeText(
                """
        buildscript {
          repositories {
            google()
            mavenLocal()
            maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
            mavenCentral()
          }
          dependencies {
            classpath 'com.android.tools.build:gradle:4.2.2'
          }
        }

        plugins {
          id 'com.android.application'
          id 'com.growingio.android.autotracker'
        }

        android {
          compileSdkVersion 30
          buildToolsVersion "30.0.2"

          defaultConfig {
            applicationId "plugin.test"
            minSdkVersion 21
            targetSdkVersion 30
          }

          compileOptions {
              sourceCompatibility 1.8
              targetCompatibility 1.8
          }
          ${additionalAndroidOptions.joinToString(separator = "\n")}
        }

        allprojects {
          repositories {
            mavenLocal()
            maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
            google()
            mavenCentral()
          }
        }

        dependencies {
          ${dependencies.joinToString(separator = "\n")}
        }

        growingAutotracker {
          ${gioOptions.joinToString(separator = "\n")}
        }
        """.trimIndent()
            )
        }
    }

    private fun writeGradleProperties() {
        gradlePropertiesFile?.delete()
        gradlePropertiesFile = tempFolder.newFile("gradle.properties").apply {
            writeText(
                """
        android.useAndroidX=true
        """.trimIndent()
            )
        }
    }

    private fun writeLocalProperties() {
        localPropertiesFile?.delete()
        localPropertiesFile = tempFolder.newFile("local.properties").apply {
            writeText(
                """
        sdk.dir=/Users/shenliming/Library/Android
        """.trimIndent()
            )
        }
    }

    private fun writeAndroidManifest() {
        manifestFile?.delete()
        manifestFile = tempFolder.newFile("/src/main/AndroidManifest.xml").apply {
            writeText(
                """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="growingio">
            <application
                android:name="${appClassName ?: "android.app.Application"}"
                >
                ${activities.joinToString(separator = "\n")}
            </application>
        </manifest>
        """.trimIndent()
            )
        }
    }

    private fun createRunner() = GradleRunner.create()
        .withProjectDir(tempFolder.root)
        .withArguments(listOf("--stacktrace", "assembleDebug") + additionalTasks)
        .withPluginClasspath()
//    .withDebug(true) // Add this line to enable attaching a debugger to the gradle test invocation
        .forwardOutput()

    // Data class representing a Gradle Test run result.
    data class Result(
        private val projectRoot: File,
        private val buildResult: BuildResult
    ) {

        val tasks: List<BuildTask> get() = buildResult.tasks

        // Finds a task by name.
        fun getTask(name: String) = buildResult.task(name) ?: error("Task '$name' not found.")

        // Gets the full build output.
        fun getOutput() = buildResult.output

        // Finds a transformed file. The srcFilePath is relative to the app's package.
        fun getTransformedFile(srcFilePath: String): File {
            val parentDir = File(projectRoot, "build/intermediates/asm_instrumented_project_classes/debug")
            return File(parentDir, srcFilePath).also {
                if (!it.exists()) {
                    error("Unable to find transformed class ${it.path}")
                }
            }
        }
    }
}
