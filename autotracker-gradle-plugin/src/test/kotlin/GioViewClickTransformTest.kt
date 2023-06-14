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
class GioViewClickTransformTest {

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
            srcPath = "growingio/ViewClickTest.java",
            srcContent =
            """
                package growingio;
                
                import android.util.Log;
                import android.view.View;
                import android.widget.CompoundButton;
                import android.widget.RadioGroup;
                import android.widget.RatingBar;
                import android.widget.SeekBar;

                /**
                 * <p>
                 *
                 * @author cpacm 2022/5/17
                 */
                public class ViewClickTest {

                    public void clickListenerTest(View view){
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Test","onClick");
                            }
                        });
                    }

                    public void seekbarTest(SeekBar seekBar){
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                Log.d("Test","onStopTrackingTouch");
                            }
                        });
                    }

                    public void radioGroupTest(RadioGroup radioGroup){
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                Log.d("Test","onCheckedChanged");
                            }
                        });
                    }

                    public void ratingBarTest(RatingBar ratingBar){
                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                Log.d("Test","onRatingChanged");
                            }
                        });
                    }

                    public void compoundButtonTest(CompoundButton button){
                        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Log.d("Test","onCheckedChanged");
                            }
                        });
                    }
                }

            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/ViewClickTest$1.class")
        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onClick" && it.desc == "(Landroid/view/View;)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(17)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("viewOnClick")
                        assertThat(method.desc).isEqualTo("(Landroid/view/View${'$'}OnClickListener;Landroid/view/View;)V")
                        true
                    }
            }
        }

        val transformedClass2 = result.getTransformedFile("growingio/ViewClickTest$2.class")
        FileInputStream(transformedClass2).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onStopTrackingTouch" && it.desc == "(Landroid/widget/SeekBar;)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(17)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("seekBarOnSeekBarChange")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/SeekBar${'$'}OnSeekBarChangeListener;Landroid/widget/SeekBar;)V")
                        true
                    }
            }
        }

        val transformedClass3 = result.getTransformedFile("growingio/ViewClickTest$3.class")
        FileInputStream(transformedClass3).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onCheckedChanged" && it.desc == "(Landroid/widget/RadioGroup;I)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(20)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("radioGroupOnChecked")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/RadioGroup${'$'}OnCheckedChangeListener;Landroid/widget/RadioGroup;I)V")
                        true
                    }
            }
        }

        val transformedClass4 = result.getTransformedFile("growingio/ViewClickTest$4.class")
        FileInputStream(transformedClass4).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onRatingChanged" && it.desc == "(Landroid/widget/RatingBar;FZ)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(23)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("ratingBarOnRatingBarChange")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/RatingBar${'$'}OnRatingBarChangeListener;Landroid/widget/RatingBar;FZ)V")
                        true
                    }
            }
        }

        val transformedClass5 = result.getTransformedFile("growingio/ViewClickTest$5.class")
        FileInputStream(transformedClass5).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "onCheckedChanged" && it.desc == "(Landroid/widget/CompoundButton;Z)V" }.let {
                assertThat(it).isNotNull()
                assertThat(it?.instructions?.size()).isEqualTo(20)
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/ViewClickInjector")
                        assertThat(method.name).isEqualTo("compoundButtonOnChecked")
                        assertThat(method.desc).isEqualTo("(Landroid/widget/CompoundButton${'$'}OnCheckedChangeListener;Landroid/widget/CompoundButton;Z)V")
                        true
                    }
            }
        }
    }

}