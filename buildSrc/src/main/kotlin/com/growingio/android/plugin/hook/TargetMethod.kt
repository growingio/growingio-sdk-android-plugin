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

class TargetMethod(val name: String, val desc: String) {
    private val mInjectMethods: MutableSet<InjectMethod> = HashSet()
    fun addInjectMethod(method: InjectMethod) {
        mInjectMethods.add(method)
    }

    fun addInjectMethods(methods: Set<InjectMethod>?) {
        if(methods==null) return
        mInjectMethods.addAll(methods)
    }

    val injectMethods: Set<InjectMethod>
        get() = Collections.unmodifiableSet(mInjectMethods)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as TargetMethod
        return if (name != that.name) false else desc == that.desc
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }
}