package com.growingio.android.plugin.util

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.Component

internal class ComponentCompatApi72Impl(private val component: Component) : ComponentCompat() {

    override val name: String
        get() = component.name

    @Suppress("UnstableApiUsage")
    override fun <ParamT : InstrumentationParameters> transformClassesWith(
        classVisitorFactoryImplClass: Class<out AsmClassVisitorFactory<ParamT>>,
        scope: InstrumentationScope,
        instrumentationParamsConfig: (ParamT) -> Unit
    ) {
        component.instrumentation.transformClassesWith(
            classVisitorFactoryImplClass,
            scope,
            instrumentationParamsConfig
        )
    }

    @Suppress("UnstableApiUsage")
    override fun setAsmFramesComputationMode(mode: FramesComputationMode) {
        component.instrumentation.setAsmFramesComputationMode(mode)
    }
}