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

package com.growingio.android.plugin.visitor

import com.growingio.android.plugin.hook.HookClassesConfig
import com.growingio.android.plugin.hook.TargetMethod
import com.growingio.android.plugin.utils.info
import com.growingio.android.plugin.utils.normalize
import com.growingio.android.plugin.utils.unNormalize
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

/**
 * <p>
 *     优化了原版的 DesugarVisit
 * @author cpacm 2022/4/19
 */
internal class DesugarClassVisitor(
    api: Int, ncv: ClassVisitor, classContext: ClassContextCompat
) : ClassVisitor(api, ncv), ClassContextCompat by classContext {

    private val generateMethodBlocks = hashMapOf<String, GenerateMethodBlock>()
    private val needInjectTargetMethods = hashSetOf<TargetMethod>()
    private var generateMethodIndex = 0

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return DesugarMethodVisitor(api, mv, access, name, descriptor)
    }

    override fun visitEnd() {
        if (generateMethodBlocks.isEmpty()) {
            super.visitEnd()
            return
        }
        for (methodBlock in generateMethodBlocks.values) {
            generateMethod(methodBlock)
        }
        super.visitEnd()
    }

    private fun generateMethod(methodBlock: GenerateMethodBlock) {
        info("[GenerateMethod]${methodBlock.methodName}#${methodBlock.methodDesc}")
        val visitor = super.visitMethod(methodBlock.access, methodBlock.methodName, methodBlock.methodDesc, null, null)
        val adapter = GeneratorAdapter(visitor, methodBlock.access, methodBlock.methodName, methodBlock.methodDesc)
        adapter.visitCode()
        val arguments = Type.getArgumentTypes(methodBlock.methodDesc)
        val isStaticOrigin = methodBlock.originHandle.tag == Opcodes.H_INVOKESTATIC

        // 插入hook before
        for (injectMethod in methodBlock.targetMethod.injectMethods) {
            if (!injectMethod.isAfter) {
                adapter.visitInsn(Opcodes.ACONST_NULL)
                if (isStaticOrigin) {
                    adapter.loadArgs()
                } else {
                    if (arguments.size > 1) {
                        adapter.loadArgs(1, arguments.size - 1)
                    }
                }
                adapter.invokeStatic(
                    Type.getObjectType(injectMethod.className),
                    Method(injectMethod.methodName, injectMethod.methodDesc)
                )
            }
        }
        // 调用原指向方法
        adapter.loadArgs()
        val owner = Type.getObjectType(methodBlock.originHandle.owner)
        val method = Method(methodBlock.originHandle.name, methodBlock.originHandle.desc)
        when (methodBlock.originHandle.tag) {
            Opcodes.H_INVOKEINTERFACE -> adapter.invokeInterface(owner, method)
            //error("should not has invoke special: ${methodBlock.methodName}#${methodBlock.methodDesc}")
            Opcodes.H_INVOKESTATIC -> adapter.invokeStatic(owner, method)
            Opcodes.H_INVOKEVIRTUAL, Opcodes.H_INVOKESPECIAL -> adapter.invokeVirtual(owner, method)
        }

        // 插入hook after
        for (injectMethod in methodBlock.targetMethod.injectMethods) {
            if (injectMethod.isAfter) {
                adapter.visitInsn(Opcodes.ACONST_NULL)
                if (isStaticOrigin) {
                    adapter.loadArgs()
                } else {
                    if (arguments.size > 1) {
                        adapter.loadArgs(1, arguments.size - 1)
                    }
                }
                adapter.invokeStatic(
                    Type.getObjectType(injectMethod.className), Method(injectMethod.methodName, injectMethod.methodDesc)
                )
            }
        }

        // 加个log,防止编译优化导致的无法正常编译
        adapter.visitLdcInsn("[GenerateMethod]")
        adapter.visitLdcInsn(methodBlock.methodName)
        adapter.visitInsn(Opcodes.ICONST_0)
        adapter.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        adapter.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/growingio/android/sdk/track/log/Logger",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V",
            false
        );

        adapter.returnValue()
        adapter.visitMaxs(arguments.size, arguments.size)
        adapter.visitEnd()
    }


    inner class DesugarMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
    ) : AdviceAdapter(api, nmv, access, name, descriptor) {

        /**
         * 1. 生成的lambda方法一定会在 visitInvokeDynamicInsn 之后访问，故可直接在接下来的 onMethodEnter 和 onMethodExit 处理
         * 2. 方法引用会存在方法已被访问的情况。如LambdaSample#methodClick
         */
        override fun visitInvokeDynamicInsn(
            name: String,
            descriptor: String,
            bootstrapMethodHandle: Handle?,
            vararg bootstrapMethodArguments: Any?
        ) {
            val index = descriptor.lastIndexOf(")L")
            if (index == -1) {
                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                return
            }
            val interfaceClazzName = descriptor.substring(index + 2, descriptor.length - 1)
            val targetClass = HookClassesConfig.superHookClasses.get(interfaceClazzName)
            if (targetClass == null) {
                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                return
            }
            val lambdaMethodDesc = (bootstrapMethodArguments[0] as Type).descriptor
            val targetMethod = targetClass.getTargetMethod(name, lambdaMethodDesc)
            if (targetMethod == null) {
                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                return
            }
            //info("[visitInvokeDynamicInsn]${className}-${name}==>${(bootstrapMethodArguments[1] as Handle).name}")
            val handle = bootstrapMethodArguments[1] as Handle
            if (name == handle.name) {
                // 校验实现方法是不是实现了对应接口的实现方法， 如果是则过滤，交给 InjectSuperClassVisitor 进行处理
                // 示例参考 LambdaSample#print1
                if (isAssignable(handle.owner, interfaceClazzName)) {
                    info("[visitInvokeDynamicInsn]${className}-${name}:skipped")
                    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                    return
                }
            }

            if (handle.owner == className.unNormalize()) {
                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)

                // 实现方法在当前类中
                // 示例参考1: 在类中生成相应的方法如 LambdaSample#print0 ==> 生成 lambda$print0$0，将其加入 injectMethods 中

                // 示例参考2: 直接在类中的引用如 LambdaSample#print2
                // 但是在示例参考2会出现以下问题：
                //      1. 方法已被访问；如 methodClick方法
                //      2. 若方法被多个 inject 注入，则会重复，如 LambdaSample#print3 和 LambdaSample#print4
                // 所以需要生成一个新的方法用于跳转

                // 条件1，判断是否是生成的 lambda 表达式
                if (handle.tag == Opcodes.H_INVOKESTATIC && handle.name.contains("lambda")) {
                    val needInjectMethod = TargetMethod(handle.name, handle.desc)
                    needInjectMethod.addInjectMethods(targetMethod.injectMethods)
                    needInjectTargetMethods.add(needInjectMethod)
                    return
                }
            }
            // 条件2 和外部类规则类似
            // 实现方法在类外部，此时需要生成一个类作为跳转，参考 LambdaSample#print5
            val key = interfaceClazzName + handle.owner + handle.name + handle.desc + generateMethodIndex

            val methodDesc = when (handle.tag) {
                H_INVOKESTATIC -> "(${handle.desc.replace("(", "")}"
                else -> "(L${handle.owner};${handle.desc.replace("(", "")}"
            }
            val access = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC
            val methodBlock =
                GenerateMethodBlock("lambda\$GIO\$" + generateMethodIndex, methodDesc, targetMethod, handle, access)
            generateMethodBlocks.put(key, methodBlock)
            generateMethodIndex++

            // handle redirect
            val newArgs = ArrayList<Any?>(bootstrapMethodArguments.size)
            for (i in bootstrapMethodArguments.indices) {
                newArgs.add(i, bootstrapMethodArguments[i])
            }
            newArgs[1] = Handle(
                H_INVOKESTATIC,
                className.unNormalize(),
                methodBlock.methodName,
                methodBlock.methodDesc,
                false
            )

            val newDesc = when (handle.tag) {
                H_INVOKESTATIC -> descriptor
                else -> {
                    val firstSemiColon: Int = descriptor.indexOf(';')
                    "(L" + handle.owner + descriptor.substring(firstSemiColon)
                }
            }
            super.visitInvokeDynamicInsn(name, newDesc, bootstrapMethodHandle, *newArgs.toArray())

        }

        override fun onMethodEnter() {
            val targetMethod = findTargetMethod(name, methodDesc) ?: return
            for (injectMethod in targetMethod.injectMethods) {
                if (!injectMethod.isAfter) {
                    visitInsn(ACONST_NULL)
                    val injectArgsLen = Type.getArgumentTypes(injectMethod.methodDesc).size - 1
                    val originArgsLen = Type.getArgumentTypes(methodDesc).size
                    if (injectArgsLen != 0) {
                        loadArgs(originArgsLen - injectArgsLen, injectArgsLen)
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                }
            }
        }

        override fun onMethodExit(opcode: Int) {
            val targetMethod = findTargetMethod(name, methodDesc) ?: return
            for (injectMethod in targetMethod.injectMethods) {
                if (injectMethod.isAfter) {
                    visitInsn(ACONST_NULL)
                    val injectArgsLen = Type.getArgumentTypes(injectMethod.methodDesc).size - 1
                    val originArgsLen = Type.getArgumentTypes(methodDesc).size
                    if (injectArgsLen != 0) {
                        loadArgs(originArgsLen - injectArgsLen, injectArgsLen)
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                }
            }
            needInjectTargetMethods.remove(targetMethod)
        }

        private fun findTargetMethod(name: String, desc: String): TargetMethod? {
            if (needInjectTargetMethods.isEmpty()) {
                return null
            }
            for (targetMethod in needInjectTargetMethods) {
                if (name == targetMethod.name && desc == targetMethod.desc) {
                    return targetMethod
                }
            }
            return null
        }
    }

    data class GenerateMethodBlock(
        val methodName: String,
        val methodDesc: String,
        val targetMethod: TargetMethod,
        val originHandle: Handle,
        val access: Int
    )
}

