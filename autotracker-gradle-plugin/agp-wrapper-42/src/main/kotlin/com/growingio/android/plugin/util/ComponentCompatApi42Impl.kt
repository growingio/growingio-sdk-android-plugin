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

/**
 * <p>
 *
 * @author cpacm 2022/6/9
 */
import com.android.build.api.component.AndroidTest
import com.android.build.api.component.Component
import com.android.build.api.component.UnitTest
import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.Variant

@Suppress("UnstableApiUsage")
internal class ComponentCompatApi42Impl(private val component: Component) : ComponentCompat() {

    override val name: String
        get() = component.name

    override fun <ParamT : InstrumentationParameters> transformClassesWith(
        classVisitorFactoryImplClass: Class<out AsmClassVisitorFactory<ParamT>>,
        scope: InstrumentationScope,
        instrumentationParamsConfig: (ParamT) -> Unit
    ) {
        component.transformClassesWith(classVisitorFactoryImplClass, scope, instrumentationParamsConfig)
    }

    override fun setAsmFramesComputationMode(mode: FramesComputationMode) {
        component.setAsmFramesComputationMode(mode)
    }

    override fun getComponentVariant(): Variant {
        if (component is Variant) {
            return component
        }
        if (component is UnitTest) {
            return component.testedVariant
        }

        if (component is AndroidTest) {
            return component.testedVariant
        }

        throw IllegalAccessException("error component type")
    }
}