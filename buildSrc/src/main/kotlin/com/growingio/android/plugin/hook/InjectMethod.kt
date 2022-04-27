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

class InjectMethod(val className: String, val methodName: String, val methodDesc: String, val isAfter: Boolean) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as InjectMethod
        if (isAfter != that.isAfter) return false
        if (className != that.className) return false
        return if (methodName != that.methodName) false else methodDesc == that.methodDesc
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + methodDesc.hashCode()
        result = 31 * result + if (isAfter) 1 else 0
        return result
    }
}