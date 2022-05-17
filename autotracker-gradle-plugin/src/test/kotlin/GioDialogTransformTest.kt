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
class GioDialogTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    val fragments = arrayListOf(
        "androidx.fragment.app.Fragment",
    )

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun testDialog(){
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
        )
        gradleRunner.addSrc(
            srcPath = "growingio/TestDialog.java",
            srcContent ="""
                package growingio;
                
                import android.app.AlertDialog;
                import android.content.Context;
                import android.content.DialogInterface;
                import android.util.Log;
                
                public class TestDialog {

                    public void createAppDialog(Context context) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("createAppDialog")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "ok");
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/TestDialog$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/content/DialogInterface;I)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("dialogOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/content/DialogInterface\$OnClickListener;Landroid/content/DialogInterface;I)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/TestDialog.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "createAppDialog" && it.desc == "(Landroid/content/Context;)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.last { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("alertDialogShow")
                        assertThat(method.desc).isEqualTo("(Landroid/app/AlertDialog;)V")
                        true
                    }
            }
        }
    }

    @Test
    fun testDialogX(){
        gradleRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
        )
        gradleRunner.addSrc(
            srcPath = "growingio/TestDialogX.java",
            srcContent ="""
                package growingio;
                
                import androidx.appcompat.app.AlertDialog;
                import android.content.Context;
                import android.content.DialogInterface;
                import android.util.Log;
                
                public class TestDialogX {

                    public void createXDialog(Context context) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("createXDialog")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "ok");
                                    }
                                })
                                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "cancel");
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/TestDialogX$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/content/DialogInterface;I)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("dialogOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/content/DialogInterface\$OnClickListener;Landroid/content/DialogInterface;I)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/TestDialogX$2.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/content/DialogInterface;I)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("dialogOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/content/DialogInterface\$OnClickListener;Landroid/content/DialogInterface;I)V")
                        true
                    }
            }
        }
    }


    @Test
    fun testDialogSupport(){
        gradleRunner.addDependencies(
            "implementation 'com.android.support:appcompat-v7:28.0.0'",
        )
        gradleRunner.addSrc(
            srcPath = "growingio/TestDialogSupport.java",
            srcContent ="""
                package growingio;
                
                import android.support.v7.app.AlertDialog;
                import android.content.Context;
                import android.content.DialogInterface;
                import android.util.Log;
                
                public class TestDialogSupport {

                    public void createSupportDialog(Context context) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("createXDialog")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "ok");
                                    }
                                })
                                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "cancel");
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/TestDialogSupport$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/content/DialogInterface;I)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("dialogOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/content/DialogInterface\$OnClickListener;Landroid/content/DialogInterface;I)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/TestDialogSupport$2.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/content/DialogInterface;I)V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/DialogInjector")
                        assertThat(method.name).isEqualTo("dialogOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/content/DialogInterface\$OnClickListener;Landroid/content/DialogInterface;I)V")
                        true
                    }
            }
        }
    }



}