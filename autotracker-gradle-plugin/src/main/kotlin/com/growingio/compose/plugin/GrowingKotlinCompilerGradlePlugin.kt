package com.growingio.compose.plugin

import com.growingio.BuildConfig
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class GrowingKotlinCompilerGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        return project.provider {
            emptyList()
        }
    }

    override fun getCompilerPluginId(): String = "com.growingio.compose.plugin"

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.growingio",
            artifactId = "growingio-compose-plugin",
            version = BuildConfig.Version
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}