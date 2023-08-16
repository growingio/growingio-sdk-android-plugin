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
import com.growingio.android.plugin.hook.TargetMethod
import com.growingio.android.plugin.transform.ClassContextCompat
import com.growingio.android.plugin.util.info
import com.growingio.android.plugin.util.simpleClass
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

/**
 * <p>
 *
 * @author cpacm 2022/4/7
 */
internal class InjectSuperClassVisitor(
    api: Int, ncv: ClassVisitor, classContext: ClassContextCompat
) : ClassVisitor(api, ncv), ClassContextCompat by classContext {

    private val mTargetClasses = arrayListOf<TargetClass>()
    private val mOverrideMethods = hashSetOf<TargetMethod>()
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
        // 使用 superName 来查找类，避免多重继承下的重复注入
        val targetClass = HookClassesConfig.superHookClasses[superName]
        if (targetClass != null) {
            mTargetClasses.add(targetClass)
        }
        for (i in interfaces!!) {
            val targetInterface = HookClassesConfig.superHookClasses[i]
            if (targetInterface != null) {
                targetInterface.setInterface(true)
                mTargetClasses.add(targetInterface)
            }
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
                mOverrideMethods.add(targetMethod)
                return InjectSuperMethodVisitor(
                    api, mv, access, name, descriptor,
                    targetMethod.injectMethods
                )
            }
        }

        return mv
    }

    override fun visitEnd() {
        // 生成未实现的 override 方法
        for (targetClass in mTargetClasses) {
            // 若是该方法是需要接口实现的，则不做方法生成
            if (targetClass.isInterface) return
            for (targetMethod in targetClass.targetMethods) {
                if (!mOverrideMethods.contains(targetMethod)) {
                    val injectMethods = targetMethod.injectMethods
                    val m = Method(targetMethod.name, targetMethod.desc)
                    val mg = GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cv)
                    injectMethod(mg, injectMethods, false, targetMethod.name, targetMethod.desc)
                    mg.loadThis()
                    mg.loadArgs()
                    mg.invokeConstructor(
                        Type.getObjectType(targetClass.name),
                        Method(targetMethod.name, targetMethod.desc)
                    )
                    injectMethod(mg, injectMethods, true, targetMethod.name, targetMethod.desc)
                    mg.returnValue()
                    mg.endMethod()
                }
            }
        }
        super.visitEnd()
    }

    private fun injectMethod(
        mg: GeneratorAdapter,
        injectMethods: Set<InjectMethod>,
        isAfter: Boolean,
        targetName: String, targetDesc: String,
    ) {
        for (injectMethod in injectMethods) {
            if (injectMethod.isAfter == isAfter && classIncluded(injectMethod.className)) {
                mg.loadThis()
                mg.loadArgs()
                mg.invokeStatic(
                    Type.getObjectType(injectMethod.className),
                    Method(injectMethod.methodName, injectMethod.methodDesc)
                )
                info((if (isAfter) "[SuperAfter] " else "[SuperBefore] ") + className.simpleClass() + "#" + targetName + targetDesc + " ==>Method Add: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
            }
        }
    }

    inner class InjectSuperMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        private val methodName: String?,//ASM6.0 hasn't inline name field,so we just create it.
        private val descriptor: String?,
        private val injectMethods: Set<InjectMethod>
    ) : AdviceAdapter(api, nmv, access, methodName, descriptor) {

        lateinit var localVariables: IntArray

        override fun onMethodEnter() {
            val targetArgs: Array<Type> = Type.getArgumentTypes(descriptor)
            localVariables = IntArray(targetArgs.size + 1)

            loadThis()
            localVariables[0] = newLocal(classType)
            storeLocal(localVariables[0])

            for (i in targetArgs.indices) {
                loadArg(i)
                localVariables[i+1] = newLocal(targetArgs[i])
                storeLocal(localVariables[i+1])
            }

            super.onMethodEnter()
            injectMethod(this, injectMethods, false, methodName.toString(), methodDesc)
        }

        override fun onMethodExit(opcode: Int) {
            injectMethodExit(injectMethods)
            super.onMethodExit(opcode)
        }

        private fun injectMethodExit(injectMethods: Set<InjectMethod>) {
            for (injectMethod in injectMethods) {
                if (injectMethod.isAfter && classIncluded(injectMethod.className)) {
                    loadLocal(localVariables[0])
                    val args: Array<Type> = Type.getArgumentTypes(descriptor)
                    for (index in args.indices) {
                        loadLocal(localVariables[index + 1])
                    }
                    invokeStatic(
                        Type.getObjectType(injectMethod.className),
                        Method(injectMethod.methodName, injectMethod.methodDesc)
                    )
                    info("[SuperAfter] " + className.simpleClass() + "#" + methodName.toString() + methodDesc + " ==>Method Add: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
                }
            }
        }
    }
}