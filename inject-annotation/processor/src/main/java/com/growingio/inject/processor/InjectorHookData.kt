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

package com.growingio.inject.processor

/**
 * <p>
 *
 * @author cpacm 2022/5/9
 */
class InjectorHookData(val injectClassName: String) {

    lateinit var injectMethodName: String
    lateinit var injectMethodDesc: String
    lateinit var targetClassName: String
    lateinit var targetMethodName: String
    lateinit var targetMethodDesc: String
    var targetMethodReturnDesc: String = ""
    var isAfter: Boolean = false

    constructor(className: String, method: String, methodDesc: String) : this(className) {
        this.injectMethodName = method
        this.injectMethodDesc = methodDesc
    }

}
