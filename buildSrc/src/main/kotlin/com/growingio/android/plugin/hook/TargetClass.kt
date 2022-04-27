/*
 * Copyright (C) 2020 Beijing Yishu Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.growingio.android.plugin.hook

import java.util.*

class TargetClass(val name: String) {
    private val mTargetMethods: MutableSet<TargetMethod> = HashSet()
    var isInterface = false
        private set

    fun addTargetMethod(method: TargetMethod) {
        mTargetMethods.add(method)
    }

    val targetMethods: Set<TargetMethod>
        get() = Collections.unmodifiableSet(mTargetMethods)

    fun getTargetMethod(name: String?, desc: String?): TargetMethod? {
        for (method in mTargetMethods) {
            if (name == method.name && desc == method.desc) {
                return method
            }
        }
        return null
    }

    fun setInterface(anInterface: Boolean) {
        isInterface = anInterface
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as TargetClass
        return name == that.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}