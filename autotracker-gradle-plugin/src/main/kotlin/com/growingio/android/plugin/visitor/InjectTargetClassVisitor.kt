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
import com.growingio.android.plugin.hook.InjectMethod
import com.growingio.android.plugin.hook.TargetClass
import com.growingio.android.plugin.transform.ClassContextCompat
import com.growingio.android.plugin.util.info
import com.growingio.android.plugin.util.simpleClass
import com.growingio.android.plugin.util.w
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

/**
 * <p>
 *
 * @author cpacm 2022/5/21
 */
internal class InjectTargetClassVisitor(
    api: Int, ncv: ClassVisitor, classContext: ClassContextCompat
) : ClassVisitor(api, ncv), ClassContextCompat by classContext {

    private val mTargetClasses = arrayListOf<TargetClass>()
    private lateinit var classType: Type

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        classType = Type.getObjectType(name)
        val targetClass = HookClassesConfig.targetHookClasses[name]
        if (targetClass != null) {
            mTargetClasses.add(targetClass)
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        for (targetClass in mTargetClasses) {
            val targetMethod = targetClass.getTargetMethod(name, descriptor)
            if (targetMethod != null) {
                return InjectSuperMethodVisitor(
                    api, mv, access, name, descriptor,
                    targetMethod.injectMethods
                )
            }
        }

        return mv
    }

    inner class InjectSuperMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        private val methodName: String?,// ASM6.0 hasn't inline name field,so we just create it.
        private val descriptor: String?,
        private val injectMethods: Set<InjectMethod>
    ) : AdviceAdapter(api, nmv, access, methodName, descriptor) {

        lateinit var localVariables: IntArray;

        override fun onMethodEnter() {
            val targetArgs: Array<Type> = Type.getArgumentTypes(descriptor)
            val thisSlot = if (methodAccess and Opcodes.ACC_STATIC == 0) 1 else 0
            if (thisSlot != 0) {
                localVariables = IntArray(targetArgs.size + 1)
                loadThis()
                localVariables[0] = newLocal(classType)
                storeLocal(localVariables[0])
            } else {
                localVariables = IntArray(targetArgs.size)
            }

            for (i in targetArgs.indices) {
                loadArg(i)
                localVariables[i + thisSlot] = newLocal(targetArgs[i])
                storeLocal(localVariables[i + thisSlot])
            }

            super.onMethodEnter()
            injectMethod(injectMethods, false, Opcodes.RETURN)
        }

        override fun onMethodExit(opcode: Int) {
            injectMethod(injectMethods, true, opcode)
            super.onMethodExit(opcode)
        }

        private fun injectMethod(
            injectMethods: Set<InjectMethod>,
            isAfter: Boolean,
            opcode: Int,
        ) {
            for (injectMethod in injectMethods) {
                if (!classIncluded(injectMethod.className)) {
                    w("can't find class:" + injectMethod.className)
                    continue
                }
                if (injectMethod.isAfter == isAfter) {
                    when (visitCode(isAfter, injectMethod.methodDesc, opcode)) {
                        1 -> loadLocal(localVariables[0])
                        2 -> visitInsn(Opcodes.DUP)
                        3 -> {
                            visitInsn(Opcodes.DUP)
                            loadLocal(localVariables[0])
                        }
                        -1 -> return
                    }
                    val args: Array<Type> = Type.getArgumentTypes(descriptor)
                    val thisSlot = if (methodAccess and Opcodes.ACC_STATIC == 0) 1 else 0
                    for (index in args.indices) {
                        loadLocal(localVariables[index + thisSlot])
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                    info((if (isAfter) "[TargetAfter] " else "[TargetBefore] ") + className.simpleClass() + "#" + methodName.toString() + methodDesc + " ==>Method Add: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
                }
            }
        }

        /**
         * 可对返回类型与this类型校验
         *
         * inject 参数 - target 参数：
         * 0 不加载返回值和this对象
         * 1 非静态函数        加载this对象
         *   静态函数且有返回值  加载返回值
         * 2 非静态函数且有返回值 加载返回值和this对象
         *
         * 返回值：
         * 0 -> NOP
         * 1 -> loadThis()
         * 2 -> visitInsn(Opcodes.DUP)
         * 3 -> visitInsn(Opcodes.DUP); loadThis()
         * -1 -> 异常指令
         */
        private fun visitCode(isAfter: Boolean, injectDescriptor: String, opcode: Int): Int {
            val isStatic = (methodAccess and Opcodes.ACC_STATIC) != 0
            val hasReturnOpcode = opcode >= Opcodes.IRETURN && opcode <= Opcodes.ARETURN

            val injectArgs = Type.getArgumentTypes(injectDescriptor)
            if (injectArgs.isEmpty()) return 0
            val targetArgs = Type.getArgumentTypes(methodDesc)

            if (!isAfter) {
                // 方法注入在前面时，只允许注入方法中比原方法（static除外）多一个
                return if (isStatic || injectArgs.size == targetArgs.size) 0 else if (injectArgs.size - targetArgs.size == 1) 1 else -1
            }

            when (injectArgs.size - targetArgs.size) {
                0 -> return 0
                1 -> {
                    if (!isStatic) {
                        return 1
                    }
                    if (isStatic && hasReturnOpcode) {
                        return 2
                    }
                }
                2 -> {
                    if (!isStatic && hasReturnOpcode) {
                        return 3
                    }
                }
            }

            return -1
        }
    }
}