package com.growingio.compose.plugin.compose

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.SpecialNames

class GrowingModifierWrapTransformer(
    private val pluginContext: IrPluginContext,
    private val growingModifierTagFunctionRef: IrSimpleFunctionSymbol,
    private val modifierCompanionClassRef: IrClassSymbol,
    private val modifierThen: IrSimpleFunctionSymbol,
    private val modifierType: IrSimpleType,
) : IrElementTransformerVoidWithContext() {

    companion object {
        val composableAnnotation = FqName("androidx.compose.runtime.Composable")
        val kotlinNothing = FqName("kotlin.Nothing")
        val modifierClassFqName = FqName("androidx.compose.ui.Modifier")
    }


    // a stack of the function names
    private var visitingFunctionNames = ArrayDeque<String?>()
    private var visitingDeclarationIrBuilder = ArrayDeque<DeclarationIrBuilder?>()

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val anonymous = declaration.name == SpecialNames.ANONYMOUS

        // in case of an anonymous, let's try to fallback to it's enclosing function name
        val name = if (!anonymous) declaration.name.toString() else {
            visitingFunctionNames.lastOrNull() ?: declaration.name.toString()
        }

        val isComposable = declaration.symbol.owner.hasAnnotation(composableAnnotation)

        val packageName = declaration.symbol.owner.parent.kotlinFqName.asString()

        val isAndroidXPackage = packageName.startsWith("androidx")
        val isGrowingPackage = packageName.startsWith("com.growingio.android")

        if (isComposable && !isAndroidXPackage && !isGrowingPackage) {
            visitingFunctionNames.add(name)
            visitingDeclarationIrBuilder.add(
                DeclarationIrBuilder(pluginContext, declaration.symbol)
            )
        } else {
            visitingFunctionNames.add(null)
            visitingDeclarationIrBuilder.add(null)
        }
        val irStatement = super.visitFunctionNew(declaration)
        visitingFunctionNames.removeLast()
        visitingDeclarationIrBuilder.removeLast()
        return irStatement
    }


    override fun visitCall(expression: IrCall): IrExpression {
        val composableName = visitingFunctionNames.lastOrNull() ?: expression.symbol.owner.name.toString()
        val builder = visitingDeclarationIrBuilder.lastOrNull() ?: DeclarationIrBuilder(pluginContext, expression.symbol)

        // avoid infinite recursion by instrumenting ourselves
        val dispatchReceiver = expression.dispatchReceiver
        if (dispatchReceiver is IrCall && dispatchReceiver.symbol == growingModifierTagFunctionRef) {
            return super.visitCall(expression)
        }

        val callName = expression.symbol.owner.name.toString()

        for (idx in 0 until expression.symbol.owner.valueParameters.size) {
            val valueParameter = expression.symbol.owner.valueParameters[idx]
            if (valueParameter.type.classFqName == modifierClassFqName) {
                val argument = expression.getValueArgument(idx)
                expression.putValueArgument(idx, wrapExpressionModifier(argument, composableName, callName, builder))
            }
        }
        return super.visitCall(expression)
    }

    private fun wrapExpressionModifier(
        expression: IrExpression?,
        composableName: String,
        callName: String,
        builder: DeclarationIrBuilder
    ): IrExpression {
        val overwriteModifier = expression == null ||
                (expression is IrComposite && expression.type.classFqName == kotlinNothing)

        if (overwriteModifier) {
            // Case A: modifier is not supplied
            // -> simply set our modifier as param
            // e.g. BasicText(text = "abc")
            // into BasicText(text = "abc", modifier = Modifier.growingTag("<composable>","BasicText")

            // Modifier.growingTag()
            return generateGrowingTagCall(builder, composableName, callName)
        } else {
            // Case B: modifier is already supplied
            // -> chain the modifiers
            // e.g. BasicText(text = "abc", modifier = Modifier.fillMaxSize())
            // into BasicText(text = "abc", modifier = Modifier.growingTag("<composable>","BasicText").then(Modifier.fillMaxSize())

            // Modifier.growingTag()
            val growingTagCall = generateGrowingTagCall(builder, composableName, callName)

            // Modifier.then()
            val thenCall = builder.irCall(
                modifierThen,
                modifierType,
                1,
                0,
                null
            )
            thenCall.putValueArgument(0, expression)
            thenCall.dispatchReceiver = growingTagCall

            return thenCall
        }
    }

    private fun generateGrowingTagCall(
        builder: DeclarationIrBuilder,
        composableName: String,
        callName: String,
    ): IrCall {
        val growingTagCall = builder.irCall(
            growingModifierTagFunctionRef,
            modifierType,
            2,
            0,
            null
        ).also {
            it.extensionReceiver = builder.irGetObjectValue(
                type = modifierCompanionClassRef.createType(false, emptyList()),
                classSymbol = modifierCompanionClassRef
            )
            it.putValueArgument(0, builder.irString(composableName))
            it.putValueArgument(1, builder.irString(callName))
        }
        return growingTagCall
    }
}