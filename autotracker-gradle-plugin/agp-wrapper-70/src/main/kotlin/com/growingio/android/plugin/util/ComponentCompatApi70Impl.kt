package com.growingio.android.plugin.util

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.Component
import com.android.build.api.variant.Variant

@Suppress("UnstableApiUsage")
internal class ComponentCompatApi70Impl(private val component: Component) : ComponentCompat() {

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

    override fun getComponentVariant(): Variant? {
        if (component is Variant) {
            return component
        }
        // unitTest and androidTest isn't an variant
        return null
    }
}