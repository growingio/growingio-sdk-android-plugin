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
import com.growingio.android.plugin.hook.HookInjectorClass
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
class GioMenuItemTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun testMenuItem() {
        gradleRunner.addSrc(
            srcPath = "growingio/MenuItemTest.java",
            srcContent =
            """
                package growingio;
                
                import android.content.Context;
                import android.view.MenuItem;
                import android.view.View;
                import android.widget.ActionMenuView;
                import android.widget.PopupMenu;
                import android.widget.Toolbar;
                /**
                 * <p>
                 *
                 * @author cpacm 2022/5/13
                 */
                public class MenuItemTest {
                    public void toolbarTest(Toolbar toolbar) {
                        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return false;
                            }
                        });
                    }
                
                    public void actionMenuViewTest(ActionMenuView menuView) {
                        menuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return false;
                            }
                        });
                    }
                
                    public void popupWindowTest(Context context, View anchorView) {
                        PopupMenu popupMenu = new PopupMenu(context, anchorView);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                }

            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/MenuItemTest$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onMenuItemClick" && it.desc == "(Landroid/view/MenuItem;)Z" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/MenuItemInjector")
                        assertThat(method.name).isEqualTo("toolbarOnMenuItemClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/Toolbar${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/MenuItemTest$2.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onMenuItemClick" && it.desc == "(Landroid/view/MenuItem;)Z" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/MenuItemInjector")
                        assertThat(method.name).isEqualTo("actionMenuViewOnMenuItemClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/ActionMenuView${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V")
                        true
                    }
            }
        }

        val transformedClass3 = result.getTransformedFile("growingio/MenuItemTest$3.class")
        FileInputStream(transformedClass3).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onMenuItemClick" && it.desc == "(Landroid/view/MenuItem;)Z" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/MenuItemInjector")
                        assertThat(method.name).isEqualTo("popupMenuOnMenuItemClick")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/PopupMenu${'$'}OnMenuItemClickListener;Landroid/view/MenuItem;)V")
                        true
                    }
            }
        }
    }

}