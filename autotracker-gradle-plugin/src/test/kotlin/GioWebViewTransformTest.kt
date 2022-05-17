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
class GioWebViewTransformTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    lateinit var gradleRunner: GradleTestRunner

    @Before
    fun setup() {
        gradleRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun testWebView() {
        gradleRunner.addSrc(
            srcPath = "growingio/WebViewTest.java",
            srcContent =
            """
                package growingio;
                
                import android.content.Context;
                import java.util.HashMap;
                
                public class WebViewTest extends android.webkit.WebView {
                
                    public WebViewTest(Context context) {
                        super(context);
                    }
                
                    private void loadUrl1() {
                        loadUrl("https://www.cpacm.net");
                    }
                
                    private void loadUrl2() {
                        HashMap<String, String> header = new HashMap<>();
                        header.put("name", "cpacm");
                        loadUrl("https://www.cpacm.net", header);
                    }
                
                    private void loadUrl3() {
                        loadData("this is test data", "application/json", "UTF-8");
                    }
                
                    private void loadUrl4() {
                        loadDataWithBaseURL("https://www.cpacm.net", "this is test data", "application/json", "UTF-8", "https://cpacm.8bgm.com");
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/WebViewTest.class")

        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "loadUrl1" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/WebViewInjector")
                        assertThat(method.name).isEqualTo("webkitWebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Landroid/webkit/WebView;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl2" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.filter { methodInsnNode -> methodInsnNode.opcode == Opcodes.INVOKESTATIC }
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/WebViewInjector")
                        assertThat(method.name).isEqualTo("webkitWebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Landroid/webkit/WebView;Ljava/lang/String;Ljava/util/Map;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl3" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/WebViewInjector")
                        assertThat(method.name).isEqualTo("webkitWebViewLoadData")
                        assertThat(method.desc).isEqualTo("(Landroid/webkit/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl4" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/WebViewInjector")
                        assertThat(method.name).isEqualTo("webkitWebViewLoadDataWithBaseURL")
                        assertThat(method.desc).isEqualTo("(Landroid/webkit/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }
        }
    }

    @Test
    fun testUcWebView() {
        gradleRunner.addSrc(
            srcPath = "com/uc/webview/export/WebView.java",
            srcContent = """
                package com.uc.webview.export;
                
                import java.util.Map;
                import android.content.Context;

                /**
                 * <p>
                 * 除去mPass外无法直接在线上依赖UCWebView,所以直接创建Webview以便测试
                 *
                 * @author cpacm 2022/5/12
                 */
                public class WebView {
                
                    public WebView(Context var1){}
                    
                    public void loadData(String var1, String var2, String var3) {
                    }

                    public void loadDataWithBaseURL(String var1, String var2, String var3, String var4, String var5) {
                    }

                    public void loadUrl(String var1) {
                    }

                    public void loadUrl(String var1, Map<String, String> var2) {
                    }
                }
            """.trimIndent()
        )
        gradleRunner.addSrc(
            srcPath = "growingio/UcWebViewTest.java",
            srcContent =
            """
                package growingio;
                
                import android.content.Context;
                import java.util.HashMap;
                
                public class UcWebViewTest extends com.uc.webview.export.WebView {
                
                    public UcWebViewTest(Context context) {
                        super(context);
                    }
                
                    private void loadUrl1() {
                        loadUrl("https://www.cpacm.net");
                    }
                
                    private void loadUrl2() {
                        HashMap<String, String> header = new HashMap<>();
                        header.put("name", "cpacm");
                        loadUrl("https://www.cpacm.net", header);
                    }
                
                    private void loadUrl3() {
                        loadData("this is test data", "application/json", "UTF-8");
                    }
                
                    private void loadUrl4() {
                        loadDataWithBaseURL("https://www.cpacm.net", "this is test data", "application/json", "UTF-8", "https://cpacm.8bgm.com");
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/UcWebViewTest.class")

        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "loadUrl1" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/UcWebViewInjector")
                        assertThat(method.name).isEqualTo("ucWebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Lcom/uc/webview/export/WebView;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl2" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.filter { methodInsnNode -> methodInsnNode.opcode == Opcodes.INVOKESTATIC }
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/UcWebViewInjector")
                        assertThat(method.name).isEqualTo("ucWebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/util/Map;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl3" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/UcWebViewInjector")
                        assertThat(method.name).isEqualTo("ucWebViewLoadData")
                        assertThat(method.desc).isEqualTo("(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl4" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/UcWebViewInjector")
                        assertThat(method.name).isEqualTo("ucWebViewLoadDataWithBaseURL")
                        assertThat(method.desc).isEqualTo("(Lcom/uc/webview/export/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }
        }
    }

    @Test
    fun testX5WebView() {
        gradleRunner.addDependencies("implementation 'com.tencent.tbs:tbssdk:44181'")
        gradleRunner.addSrc(
            srcPath = "growingio/X5WebViewTest.java",
            srcContent =
            """
                package growingio;
                
                import android.content.Context;
                import java.util.HashMap;
                
                public class X5WebViewTest extends com.tencent.smtt.sdk.WebView {
                
                    public X5WebViewTest(Context context) {
                        super(context);
                    }
                
                    private void loadUrl1() {
                        loadUrl("https://www.cpacm.net");
                    }
                
                    private void loadUrl2() {
                        HashMap<String, String> header = new HashMap<>();
                        header.put("name", "cpacm");
                        loadUrl("https://www.cpacm.net", header);
                    }
                
                    private void loadUrl3() {
                        loadData("this is test data", "application/json", "UTF-8");
                    }
                
                    private void loadUrl4() {
                        loadDataWithBaseURL("https://www.cpacm.net", "this is test data", "application/json", "UTF-8", "https://cpacm.8bgm.com");
                    }
                }
            """.trimIndent()
        )

        val result = gradleRunner.build()
        val assembleTask = result.getTask(":assembleDebug")
        Assert.assertEquals(TaskOutcome.SUCCESS, assembleTask.outcome)

        val transformedClass = result.getTransformedFile("growingio/X5WebViewTest.class")

        FileInputStream(transformedClass).use { fileInput ->
            val classReader = ClassReader(fileInput.readAllBytes())
            val classNode = ClassNode()
            classReader.accept(classNode, 0)
            classNode.methods.find { it.name == "loadUrl1" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/X5WebViewInjector")
                        assertThat(method.name).isEqualTo("x5WebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl2" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.filter { methodInsnNode -> methodInsnNode.opcode == Opcodes.INVOKESTATIC }
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/X5WebViewInjector")
                        assertThat(method.name).isEqualTo("x5WebViewLoadUrl")
                        assertThat(method.desc).isEqualTo("(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/util/Map;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl3" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/X5WebViewInjector")
                        assertThat(method.name).isEqualTo("x5WebViewLoadData")
                        assertThat(method.desc).isEqualTo("(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }

            classNode.methods.find { it.name == "loadUrl4" && it.desc == "()V" }.let {
                assertThat(it).isNotNull()
                it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                    ?.first { method ->
                        assertThat(method.opcode).isEqualTo(Opcodes.INVOKESTATIC)
                        assertThat(method.owner).isEqualTo("com/growingio/android/sdk/autotrack/inject/X5WebViewInjector")
                        assertThat(method.name).isEqualTo("x5WebViewLoadDataWithBaseURL")
                        assertThat(method.desc).isEqualTo("(Lcom/tencent/smtt/sdk/WebView;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
                        true
                    }
            }
        }
    }

}