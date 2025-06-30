package com.growingio.compose.plugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class JetpackComposeTracingIrExtension(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {

    val versionString = KotlinCompilerVersion.getVersion()
    val version =
        if (versionString != null) {
            SimpleSemanticVersion.from(versionString)
        } else {
            SimpleSemanticVersion(2, 1, 20)
        }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

        val modifierClassId = FqName("androidx.compose.ui").classId("Modifier")
        val modifierClassSymbol = pluginContext.referenceClass(modifierClassId)
        if (modifierClassSymbol == null) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "No class definition of androidx.compose.ui.Modifier found, " +
                        "Kotlin Compiler plugin won't run. " +
                        "Please ensure you're applying the plugin to a compose-enabled project."
            )
            return
        }

        val modifierType = modifierClassSymbol.owner.defaultType
        val modifierCompanionClass =
            pluginContext.referenceClass(modifierClassId)?.owner?.companionObject()
        val modifierCompanionClassRef = modifierCompanionClass?.symbol

        if (modifierCompanionClass == null || modifierCompanionClassRef == null) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "No type definition of androidx.compose.ui.Modifier found, " +
                        "Kotlin Compiler plugin won't run. " +
                        "Please ensure you're applying to plugin to a compose-enabled project."
            )
            return
        }

        val modifierThenRefs = pluginContext
            .referenceFunctions(modifierClassId.callableId("then"))
        if (modifierThenRefs.isEmpty()) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "No definition of androidx.compose.ui.Modifier.then() found, " +
                        "Kotlin Compiler plugin won't run. " +
                        "Please ensure you're applying to plugin to a compose-enabled project."
            )
            return
        } else if (modifierThenRefs.size != 1) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "Multiple definitions androidx.compose.ui.Modifier.then() found."
            )
            return
        }

        val modifierThen = modifierThenRefs.single()

        val growingModifierTagFunction = FqName("com.growingio.android.compose")
            .classId("GrowingCompose")
            .callableId("autotrackElement")

        val growingModifierTagFunctionRefs = pluginContext
            .referenceFunctions(growingModifierTagFunction)

        if (growingModifierTagFunctionRefs.isEmpty()) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "com.growingio.android.compose.Modifier.growingTag() not found, " +
                        "Kotlin Compiler plugin won't run. " +
                        "Please ensure you're using " +
                        "'com.growingio.android.compose' as a dependency."
            )
            return
        } else if (growingModifierTagFunctionRefs.size != 1) {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "Multiple definitions com.growingio.android.compose.Modifier.growingTag() found."
            )
            return
        }
        val growingModifierTagFunctionRef = growingModifierTagFunctionRefs.single()

        // 2.1.20 removed some optional parameters, causing API incompatibility
        // e.g. java.lang.NoSuchMethodError
        // see https://github.com/JetBrains/kotlin/commit/dd508452c414a0ee8082aa6f76d664271cb38f2f
        val modifierWrapTransformer: IrElementTransformerVoidWithContext =
            if (version >= SimpleSemanticVersion(2, 1, 20)) {
                GrowingModifierWrapTransformer21(
                    pluginContext,
                    growingModifierTagFunctionRef,
                    modifierCompanionClassRef,
                    modifierThen,
                    modifierType
                )
            } else {
                GrowingModifierWrapTransformer19(
                    pluginContext,
                    growingModifierTagFunctionRef,
                    modifierCompanionClassRef,
                    modifierThen,
                    modifierType
                )
            }


        moduleFragment.transform(modifierWrapTransformer, null)
    }

}

fun FqName.classId(name: String): ClassId {
    return ClassId(this, Name.identifier(name))
}

fun ClassId.callableId(name: String): CallableId {
    return CallableId(this, Name.identifier(name))
}

data class SimpleSemanticVersion(val major: Int, val minor: Int, val patch: Int) :
    Comparable<SimpleSemanticVersion> {

    companion object {
        fun from(version: String): SimpleSemanticVersion {
            val parts = version.trim().split(".")
            require(parts.size == 3) { "Invalid semantic version: $version" }

            val (major, minor, patch) =
                parts.map { it.toIntOrNull() ?: error("Invalid number in version: $version") }
            return SimpleSemanticVersion(major, minor, patch)
        }
    }

    override fun compareTo(other: SimpleSemanticVersion): Int {
        return compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })
    }
}