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
import com.growingio.android.plugin.hook.TargetClass
import com.growingio.android.plugin.hook.TargetMethod
import com.growingio.android.plugin.utils.info
import com.growingio.android.plugin.utils.simpleClass
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

/**
 * <p>
 *
 * @author cpacm 2022/4/26
 */
internal class InjectAroundClassVisitor(
    api: Int, ncv: ClassVisitor, classContext: ClassContextCompat
) : ClassVisitor(api, ncv), ClassContextCompat by classContext {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return AroundMethodVisitor(api, mv, access, name, descriptor)
    }

    inner class AroundMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) : GeneratorAdapter(api, nmv, access, name, descriptor) {
        override fun visitMethodInsn(
            opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean
        ) {
            val targetMethod = findTargetMethod(owner, name, descriptor)
            if (targetMethod == null) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                return
            }

            val originalMethod = Method(name, descriptor)
            var callObject = -1
            val locals = IntArray(originalMethod.argumentTypes.size)
            for (i in locals.indices.reversed()) {
                locals[i] = newLocal(originalMethod.argumentTypes[i])
                storeLocal(locals[i])
            }
            if (opcode != Opcodes.INVOKESTATIC) {
                callObject = newLocal(Type.getObjectType(owner))
                storeLocal(callObject)
            }
            for (injectMethod in targetMethod.injectMethods) {
                if (!injectMethod.isAfter && classIncluded(injectMethod.className)) {
                    if (callObject >= 0) {
                        loadLocal(callObject)
                    }
                    for (tmpLocal in locals) {
                        loadLocal(tmpLocal)
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                    info("[AroundBefore] " + className + "#" + name + descriptor + " ==> Method Insert: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
                }
            }

            if (callObject >= 0) {
                loadLocal(callObject)
            }

            for (tmpLocal in locals) {
                loadLocal(tmpLocal)
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

            for (injectMethod in targetMethod.injectMethods) {
                if (injectMethod.isAfter && classIncluded(injectMethod.className)) {
                    if (callObject >= 0) {
                        loadLocal(callObject)
                    }
                    for (tmpLocal in locals) {
                        loadLocal(tmpLocal)
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                    info("[AroundAfter] " + className + "#" + name + descriptor + " ==> Method Insert: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
                }
            }
        }

        private fun findTargetClass(className: String): List<TargetClass> {
            val targetClasses = arrayListOf<TargetClass>()
            val aroundHookClasses = HookClassesConfig.aroundHookClasses
            for (clazz in aroundHookClasses.keys) {
                if (isAssignable(className, clazz)) {
                    aroundHookClasses.get(clazz)?.let {
                        targetClasses.add(it)
                    }
                }
            }
            return targetClasses
        }

        private fun findTargetMethod(className: String, methodName: String, methodDesc: String): TargetMethod? {
            val targetClasses = findTargetClass(className)
            for (target in targetClasses) {
                val method = target.getTargetMethod(methodName, methodDesc)
                if (method != null) return method
            }
            return null
        }
    }
}