package com.growingio.compose.plugin.compose

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

class JetpackComposeTracingIrExtension(
    private val messageCollector: MessageCollector
) : IrGenerationExtension {

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
        val modifierWrapTransformer = GrowingModifierWrapTransformer(
            pluginContext,
            growingModifierTagFunctionRef,
            modifierCompanionClassRef,
            modifierThen,
            modifierType
        )

        moduleFragment.transform(modifierWrapTransformer, null)
    }

}

fun FqName.classId(name: String): ClassId {
    return ClassId(this, org.jetbrains.kotlin.name.Name.identifier(name))
}

fun ClassId.callableId(name: String): CallableId {
    return CallableId(this, org.jetbrains.kotlin.name.Name.identifier(name))
}