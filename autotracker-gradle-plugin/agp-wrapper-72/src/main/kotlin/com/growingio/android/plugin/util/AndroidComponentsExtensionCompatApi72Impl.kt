package com.growingio.android.plugin.util

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import org.gradle.api.Project

class AndroidComponentsExtensionCompatApi72Impl(
    private val project: Project
) : AndroidComponentsExtensionCompat {

    override fun onAllVariants(block: (ComponentCompat) -> Unit, testBlock: (ComponentCompat) -> Unit) {
        val actual = project.extensions.getByType(AndroidComponentsExtension::class.java)
        actual.onVariants { variant ->
            block.invoke(ComponentCompatApi72Impl(variant))

            (variant as? HasAndroidTest)?.androidTest?.let { testBlock.invoke(ComponentCompatApi72Impl(it)) }

            variant.unitTest?.let { testBlock.invoke(ComponentCompatApi72Impl(it)) }
        }
    }
}