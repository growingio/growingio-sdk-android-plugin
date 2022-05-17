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

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * <p>
 *
 * @author cpacm 2022/4/29
 */
class GioGradlePluginTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
    }

    // Verify plugin configuration fails when runtime dependency is missing but plugin is applied.
    @Test
    fun test_missingLibraryDep() {
        gradleRunner.clearDependencies()
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'"
        )

        val result = gradleRunner.buildAndFail()
        assertThat(result.getOutput()).contains(
            "The GrowingIO Gradle plugin is applied but no growingio autotracker sdk dependency was found."
        )
    }

    @Test
    fun testAssemble() {
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
        )
        gradleRunner.addAndroidOption(
            "buildFeatures.buildConfig = false"
        )
        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)
    }
}