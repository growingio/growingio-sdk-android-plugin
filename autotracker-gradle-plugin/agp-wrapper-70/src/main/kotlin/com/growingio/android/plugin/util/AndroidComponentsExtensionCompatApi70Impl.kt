package com.growingio.android.plugin.util

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.DynamicFeatureVariant
import com.android.build.api.variant.LibraryVariant
import org.gradle.api.Project

class AndroidComponentsExtensionCompatApi70Impl(
    private val project: Project
) : AndroidComponentsExtensionCompat {

    override fun onAllVariants(block: (ComponentCompat) -> Unit, testBlock: (ComponentCompat) -> Unit) {
        val actual = project.extensions.getByType(AndroidComponentsExtension::class.java)
        actual.onVariants { variant ->
            block.invoke(ComponentCompatApi70Impl(variant))

            when (variant) {
                is ApplicationVariant -> variant.androidTest
                is LibraryVariant -> variant.androidTest
                is DynamicFeatureVariant -> variant.androidTest
                else -> null
            }?.let { testBlock.invoke(ComponentCompatApi70Impl(it)) }

            variant.unitTest?.let { testBlock.invoke(ComponentCompatApi70Impl(it)) }
        }
    }
}