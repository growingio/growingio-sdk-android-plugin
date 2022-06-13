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

package com.growingio.android.plugin.util

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope

/**
 * <p>
 * Compatibility version of [com.android.build.api.variant.Component]
 * - In AGP 4.2 its package is 'com.android.build.api.component'
 * - In AGP 7.0 its packages is 'com.android.build.api.variant'
 * @author cpacm 2022/6/9
 */
@Suppress("UnstableApiUsage") // ASM Pipeline APIs
abstract class ComponentCompat {
    /** Redeclaration of [com.android.build.api.variant.ComponentIdentity.name] */
    abstract val name: String

    /** Redeclaration of [com.android.build.api.variant.Component.transformClassesWith] */
    abstract fun <ParamT : InstrumentationParameters> transformClassesWith(
        classVisitorFactoryImplClass: Class<out AsmClassVisitorFactory<ParamT>>,
        scope: InstrumentationScope,
        instrumentationParamsConfig: (ParamT) -> Unit
    )

    /** Redeclaration of [com.android.build.api.variant.Component.setAsmFramesComputationMode] */
    abstract fun setAsmFramesComputationMode(mode: FramesComputationMode)
}