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
import com.growingio.android.plugin.utils.info
import com.growingio.android.plugin.utils.simpleClass
import com.growingio.android.plugin.utils.w
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.GeneratorAdapter
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

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
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

    private fun injectMethod(
        mg: GeneratorAdapter,
        injectMethods: Set<InjectMethod>,
        isAfter: Boolean,
        targetName: String, targetDesc: String,
    ) {
        for (injectMethod in injectMethods) {
            if (!classIncluded(injectMethod.className)) {
                w("can't find class:" + injectMethod.className)
                continue
            }
            if (injectMethod.isAfter == isAfter) {
                mg.loadThis()
                mg.loadArgs()
                mg.invokeStatic(
                    Type.getObjectType(injectMethod.className),
                    Method(injectMethod.methodName, injectMethod.methodDesc)
                )
                info((if (isAfter) "[TargetAfter] " else "[TargetBefore] ") + className.simpleClass() + "#" + targetName + targetDesc + " ==>Method Add: " + injectMethod.className.simpleClass() + "#" + injectMethod.methodName)
            }
        }
    }

    inner class InjectSuperMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        private val injectMethods: Set<InjectMethod>
    ) : AdviceAdapter(api, nmv, access, name, descriptor) {

        override fun onMethodEnter() {
            super.onMethodEnter()
            injectMethod(this, injectMethods, false, name.toString(), methodDesc)
        }

        override fun onMethodExit(opcode: Int) {
            injectMethod(this, injectMethods, true, name.toString(), methodDesc)
            super.onMethodExit(opcode)
        }
    }
}