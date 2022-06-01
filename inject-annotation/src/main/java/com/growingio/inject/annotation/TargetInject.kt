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

package com.growingio.inject.annotation

import kotlin.reflect.KClass

/**
 * <p>
 *     插入指定类的指定方法，对应 putTargetHookMethod
 * @author cpacm 2022/5/6
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class TargetInject(
    val clazz: KClass<*>,
    val method: String,
    val parameterTypes: Array<KClass<*>> = [],
    val returnType: KClass<*> = Unit::class,
    val isAfter: Boolean = false
)