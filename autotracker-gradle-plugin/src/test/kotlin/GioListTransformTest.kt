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
class GioListTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun testViewClick() {
        gradleRunner.addSrc(
            srcPath = "growingio/ListClickTest.java",
            srcContent =
            """
                package growingio;
                
                import android.util.Log;
                import android.view.View;
                import android.widget.AdapterView;
                import android.widget.ExpandableListView;

                /**
                 * <p>
                 *
                 * @author cpacm 2022/5/17
                 */
                public class ListClickTest {

                    public void adapterItemTest(AdapterView adapterView){
                        adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.d("Test","onItemClick");
                            }
                        });
                    }

                    public void adapterSelectTest(AdapterView adapterView){
                        adapterView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.d("Test","onItemSelected");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    public void expandableListGroupClickTest(ExpandableListView list){
                        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                Log.d("Test","onGroupClick");
                                return false;
                            }
                        });

                        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                Log.d("Test","onChildClick");
                                return false;
                            }
                        });

                    }
                }

            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/ListClickTest$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onItemClick" && it.desc == "(Landroid/widget/AdapterView;Landroid/view/View;IJ)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(24)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("adapterViewOnItemClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/AdapterView${'$'}OnItemClickListener;Landroid/widget/AdapterView;Landroid/view/View;IJ)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/ListClickTest$2.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onItemSelected" && it.desc == "(Landroid/widget/AdapterView;Landroid/view/View;IJ)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(24)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("adapterViewOnItemSelected")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/AdapterView${'$'}OnItemSelectedListener;Landroid/widget/AdapterView;Landroid/view/View;IJ)V")
                        true
                    }
            }
        }

        val transformedClass3 = result.getTransformedFile("growingio/ListClickTest$3.class")
        FileInputStream(transformedClass3).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onGroupClick" && it.desc == "(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(25)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("expandableListViewOnGroupClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/ExpandableListView${'$'}OnGroupClickListener;Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V")
                        true
                    }
            }
        }

        val transformedClass4 = result.getTransformedFile("growingio/ListClickTest$4.class")
        FileInputStream(transformedClass4).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onChildClick" && it.desc == "(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(28)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("expandableListViewOnChildClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/ExpandableListView${'$'}OnChildClickListener;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V")
                        true
                    }
            }
        }
    }

}