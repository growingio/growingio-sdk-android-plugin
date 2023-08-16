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
import com.growingio.android.plugin.util.asIterable
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.FileInputStream

/**
 * <p>
 *
 * @author cpacm 2022/5/5
 */
class GioFragmentXTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    val fragments = arrayListOf(
        "androidx.fragment.app.Fragment",
    )

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
        )
    }

    @Test
    fun testFragments() {
        for (index in 0 until fragments.size) {
            testFragment(index, fragments[index])
        }
    }

    private fun testFragment(index: Int, fragment: String) {
        gradleRunner.addSrc(
            srcPath = "growingio/TestFragment${index}.java",
            srcContent =
            """
                package growingio;
                
                import android.os.Bundle;
                import ${fragment};
                
                public class TestFragment${index} extends ${fragment} {
                  
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/TestFragment${index}.class")

        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onDestroyView" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(5)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.last { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/FragmentInjector")
                        assertThat(method.name).isEqualTo("androidxFragmentOnDestroyView")
                        assertThat(method.desc).isEqualTo("(Landroidx/fragment/app/Fragment;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "onResume" && it.desc == "()V" }
                .let {
                    assertThat(it).isNotNull()
                    assertThat(it?.instructions?.size()).isEqualTo(5)
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.last { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/FragmentInjector")
                            assertThat(method.name).isEqualTo("androidxFragmentOnResume")
                            assertThat(method.desc).isEqualTo("(Landroidx/fragment/app/Fragment;)V")
                            true
                        }
                }

            classNode.methods.find { it.name == "onHiddenChanged" && it.desc == "(Z)V" }
                .let {
                    assertThat(it).isNotNull()
                    assertThat(it?.instructions?.size()).isEqualTo(7)
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.last { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/FragmentInjector")
                            assertThat(method.name).isEqualTo("androidxFragmentOnHiddenChanged")
                            assertThat(method.desc).isEqualTo("(Landroidx/fragment/app/Fragment;Z)V")
                            true
                        }
                }

            classNode.methods.find { it.name == "setUserVisibleHint" && it.desc == "(Z)V" }
                .let {
                    assertThat(it).isNotNull()
                    assertThat(it?.instructions?.size()).isEqualTo(7)
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.last { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/FragmentInjector")
                            assertThat(method.name).isEqualTo("androidxFragmentSetUserVisibleHint")
                            assertThat(method.desc).isEqualTo("(Landroidx/fragment/app/Fragment;Z)V")
                            true
                        }
                }
        }
    }

}