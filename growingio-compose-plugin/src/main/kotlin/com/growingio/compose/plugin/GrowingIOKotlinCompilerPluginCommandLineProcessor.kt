package com.growingio.compose.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi


@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class GrowingIOKotlinCompilerPluginCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "com.growingio.compose.plugin"

    override val pluginOptions: Collection<AbstractCliOption> = emptyList()

}