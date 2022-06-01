/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License")
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

package com.growingio.android.plugin.hook

import java.util.*

/**
 * <p>
 *
 * @author cpacm 2022/4/12
 */
object HookClassesConfig {
    private val AROUND_HOOK_CLASSES: MutableMap<String, TargetClass> = mutableMapOf()
    private val SUPER_HOOK_CLASSES: MutableMap<String, TargetClass> = mutableMapOf()
    private val TARGET_HOOK_CLASSES: MutableMap<String, TargetClass> = mutableMapOf()

    private fun putHookMethod(
        classMap: MutableMap<String, TargetClass>, targetClassName: String,
        targetMethodName: String, targetMethodDesc: String, injectClassName: String,
        injectMethodName: String, injectMethodDesc: String, isAfter: Boolean
    ) {
        var targetClass = classMap[targetClassName]
        if (targetClass == null) {
            targetClass = TargetClass(targetClassName)
            classMap[targetClassName] = targetClass
        }
        var targetMethod = targetClass.getTargetMethod(targetMethodName, targetMethodDesc)
        if (targetMethod == null) {
            targetMethod = TargetMethod(targetMethodName, targetMethodDesc)
            targetClass.addTargetMethod(targetMethod)
        }
        targetMethod.addInjectMethod(InjectMethod(injectClassName, injectMethodName, injectMethodDesc, isAfter))
    }

    init {
        val aroundList = HookInjectorClass.initAroundClass()
        for (around in aroundList) {
            putHookMethod(
                AROUND_HOOK_CLASSES,
                around.targetClassName,
                around.targetMethodName,
                around.targetMethodDesc,
                around.injectClassName,
                around.injectMethodName,
                around.injectMethodDesc,
                around.isAfter
            )
        }

        val superList = HookInjectorClass.initSuperClass()
        for (s in superList) {
            putHookMethod(
                SUPER_HOOK_CLASSES,
                s.targetClassName,
                s.targetMethodName,
                s.targetMethodDesc,
                s.injectClassName,
                s.injectMethodName,
                s.injectMethodDesc,
                s.isAfter
            )
        }

        val targetList = HookInjectorClass.initTargetClass()
        for (t in targetList) {
            putHookMethod(
                TARGET_HOOK_CLASSES,
                t.targetClassName,
                t.targetMethodName,
                t.targetMethodDesc,
                t.injectClassName,
                t.injectMethodName,
                t.injectMethodDesc,
                t.isAfter
            )
        }
    }

    val aroundHookClasses: Map<String, TargetClass> get() = Collections.unmodifiableMap(AROUND_HOOK_CLASSES)
    val superHookClasses: Map<String, TargetClass> get() = Collections.unmodifiableMap(SUPER_HOOK_CLASSES)
    val targetHookClasses: Map<String, TargetClass> get() = Collections.unmodifiableMap(TARGET_HOOK_CLASSES)
}