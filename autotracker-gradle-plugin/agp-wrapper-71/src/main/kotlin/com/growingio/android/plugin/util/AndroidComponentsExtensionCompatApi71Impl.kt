package com.growingio.android.plugin.util

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import org.gradle.api.Project

class AndroidComponentsExtensionCompatApi71Impl(
    private val project: Project
) : AndroidComponentsExtensionCompat {

    override fun onAllVariants(block: (ComponentCompat) -> Unit) {
        val actual = project.extensions.getByType(AndroidComponentsExtension::class.java)
        actual.onVariants { variant ->
            block.invoke(ComponentCompatApi71Impl(variant))

            (variant as? HasAndroidTest)?.androidTest?.let { block.invoke(ComponentCompatApi71Impl(it)) }

            variant.unitTest?.let { block.invoke(ComponentCompatApi71Impl(it)) }
        }
    }
}