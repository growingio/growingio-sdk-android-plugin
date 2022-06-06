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
import com.growingio.android.plugin.utils.asIterable
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
class GioActivityTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    private val activities = arrayListOf(
        "android.app.Activity",
        "android.app.ActivityGroup",
        "android.app.AliasActivity",
        "android.app.ExpandableListActivity",
        "android.app.LauncherActivity",
        "android.app.ListActivity",
        "android.app.NativeActivity",
        "android.app.TabActivity",
        "android.preference.PreferenceActivity",
        "android.accounts.AccountAuthenticatorActivity"
    )

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
        )
    }

    @Test
    fun testActivities() {
        for (index in 0 until activities.size) {
            testActivity(index, activities[index])
        }
    }

    private fun testActivity(index: Int, activity: String) {
        gradleRunner.addSrc(
            srcPath = "growingio/TestActivity${index}.java",
            srcContent =
            """
                package growingio;
                
                import android.os.Bundle;
                import ${activity};
                
                public class TestActivity${index} extends ${activity} {
                  @Override
                  public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                  }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/TestActivity${index}.class")

        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onNewIntent" && it.desc == "(Landroid/content/Intent;)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(7)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ActivityInjector")
                        assertThat(method.name).isEqualTo("onActivityNewIntent")
                        assertThat(method.desc).isEqualTo("(Landroid/app/Activity;Landroid/content/Intent;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "onOptionsItemSelected" && it.desc == "(Landroid/view/MenuItem;)Z" }
                .let {
                    assertThat(it).isNotNull()
                    assertThat(it?.instructions?.size()).isEqualTo(7)
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.first { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ActivityInjector")
                            assertThat(method.name).isEqualTo("menuItemOnOptionsItemSelected")
                            assertThat(method.desc).isEqualTo("(Landroid/app/Activity;Landroid/view/MenuItem;)V")
                            true
                        }
                }

            classNode.methods.find { it.name == "onChildClick" && it.desc == "(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z" }
                .let {
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.first { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ActivityInjector")
                            assertThat(method.name).isEqualTo("expandableListActivityOnChildClick")
                            assertThat(method.desc).isEqualTo("(Landroid/app/ExpandableListActivity;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V")
                            true
                        }
                }

            classNode.methods.find { it.name == "onListItemClick" && it.desc == "(Landroid/widget/ListView;Landroid/view/View;IJ)V" }
                .let {
                    it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                        ?.first { method ->
                            assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                            assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ActivityInjector")
                            assertThat(method.name).isEqualTo("listActivityOnListItemClick")
                            assertThat(method.desc).isEqualTo("(Landroid/app/ListActivity;Landroid/widget/ListView;Landroid/view/View;IJ)V")
                            true
                        }
                }
        }
    }

}