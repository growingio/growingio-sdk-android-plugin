/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.inject.processor

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.growingio.inject.annotation.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class InjectorProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private val aroundHookClassArgs = arrayListOf<InjectorHookData>()
    private val superHookClassArgs = arrayListOf<InjectorHookData>()

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols =
            resolver.getSymbolsWithAnnotation(Belong::class.qualifiedName ?: "com.growingio.inject.annotation.Belong")
                .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        logger.info("start injector processor!")

        symbols.forEach {
            logger.info("analyze class $it")
            it.accept(Visitor(resolver), Unit)
        }

        generateHookClass()

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun generateHookClass() {

        val listClass = MUTABLE_LIST
        val targetClass = ClassName.bestGuess("com.growingio.android.plugin.hook.HookInjectorClass.HookData")
        val mapType = listClass.parameterizedBy(targetClass)
        val paramSpec1 = PropertySpec.builder(AROUND_PARAM_NAME, mapType, KModifier.PRIVATE)
            .initializer("mutableListOf()")
            .build()

        val paramSpec2 = PropertySpec.builder(SUPER_PARAM_NAME, mapType, KModifier.PRIVATE)
            .initializer("mutableListOf()")
            .build()

        val dataTypeSpec = TypeSpec.classBuilder(DATA_CLASS_NAME)
            .addModifiers(KModifier.DATA)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("targetClassName", String::class)
                    .addParameter("targetMethodName", String::class)
                    .addParameter("targetMethodDesc", String::class)
                    .addParameter("injectClassName", String::class)
                    .addParameter("injectMethodName", String::class)
                    .addParameter("injectMethodDesc", String::class)
                    .addParameter("isAfter", Boolean::class)
                    .build()
            )
            .addProperty(PropertySpec.builder("targetClassName", String::class).initializer("targetClassName").build())
            .addProperty(
                PropertySpec.builder("targetMethodName", String::class).initializer("targetMethodName").build()
            )
            .addProperty(
                PropertySpec.builder("targetMethodDesc", String::class).initializer("targetMethodDesc").build()
            )
            .addProperty(PropertySpec.builder("injectClassName", String::class).initializer("injectClassName").build())
            .addProperty(
                PropertySpec.builder("injectMethodName", String::class).initializer("injectMethodName").build()
            )
            .addProperty(
                PropertySpec.builder("injectMethodDesc", String::class).initializer("injectMethodDesc").build()
            )
            .addProperty(PropertySpec.builder("isAfter", Boolean::class).initializer("isAfter").build())
            .build()

        val aroundFuncBuilder = FunSpec.builder(AROUND_METHOD_NAME).addStatement("$AROUND_PARAM_NAME.clear()")
        for (around in aroundHookClassArgs) {
            aroundFuncBuilder.addStatement(
                "$AROUND_PARAM_NAME.add(${DATA_CLASS_NAME}(%S,%S,%S,%S,%S,%S,${around.isAfter}))",
                around.targetClassName,
                around.targetMethodName,
                around.targetMethodDesc + around.targetMethodReturnDesc,
                around.injectClassName,
                around.injectMethodName,
                around.injectMethodDesc,
            )
        }
        val aroundFunc = aroundFuncBuilder
            .returns(mapType)
            .addStatement("return $AROUND_PARAM_NAME")
            .build()

        val superFuncBuilder = FunSpec.builder(SUPER_METHOD_NAME).addStatement("$SUPER_PARAM_NAME.clear()")
        for (s in superHookClassArgs) {
            superFuncBuilder.addStatement(
                "$SUPER_PARAM_NAME.add(${DATA_CLASS_NAME}(%S,%S,%S,%S,%S,%S,${s.isAfter}))",
                s.targetClassName,
                s.targetMethodName,
                s.targetMethodDesc + s.targetMethodReturnDesc,
                s.injectClassName,
                s.injectMethodName,
                s.injectMethodDesc,
            )
        }
        val superFunc = superFuncBuilder
            .returns(mapType)
            .addStatement("return $SUPER_PARAM_NAME")
            .build()


        val typeSpec = TypeSpec.objectBuilder(CLASS_NAME)
            .addProperty(paramSpec1)
            .addProperty(paramSpec2)
            .addType(dataTypeSpec)
            .addFunction(aroundFunc)
            .addFunction(superFunc)
            .build()

        val fileSpec = FileSpec.builder(packageName = PACKAGE_NAME, CLASS_NAME)
            .addFileComment("This class is auto-generated by Inject-Processor, please don't modify it!")
            .addType(typeSpec)
            .build()

        codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = PACKAGE_NAME,
            fileName = CLASS_NAME
        ).use {
            it.writer().use {
                fileSpec.writeTo(it)
            }
        }

        codeGenerator.generatedFile.forEach {
            logger.warn(it.path)
        }
    }


    companion object {
        const val PACKAGE_NAME = "com.growingio.android.plugin.hook"
        const val CLASS_NAME = "HookInjectorClass"
        const val DATA_CLASS_NAME = "HookData"

        const val AROUND_PARAM_NAME = "AROUND_HOOK_CLASSES"
        const val AROUND_METHOD_NAME = "initAroundClass"

        const val SUPER_PARAM_NAME = "SUPER_HOOK_CLASSES"
        const val SUPER_METHOD_NAME = "initSuperClass"
    }

    inner class Visitor(private val resolver: Resolver) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.INTERFACE) {
                logger.error("Only interface can be annotated with @Belong", classDeclaration)
                return
            }

            val classAnnotation =
                classDeclaration.annotations.first { it.shortName.asString() == Belong::class.simpleName }

            // of course, className is the first arg
            val injectClass = classAnnotation.arguments.first().value.toString().unNormalize()
            logger.warn("injectClass:$injectClass")

            classDeclaration.getDeclaredFunctions().iterator().forEach { function ->
                val injectMethod = function.toString()
                logger.warn("injectMethod:$injectMethod")

                val injectReturnDesc = function.returnType.typeName(resolver)
                logger.warn("returnType:${function.returnType.typeName(resolver)}")

                val injectMethodDescBuilder = StringBuilder("(")
                function.parameters.forEach {
                    injectMethodDescBuilder.append(it.type.typeName(resolver) ?: "")
                }
                injectMethodDescBuilder.append(")").append(injectReturnDesc)
                logger.warn("injectMethodDesc:$injectMethodDescBuilder")

                function.annotations.filter {
                    it.shortName.asString() == After::class.simpleName
                            || it.shortName.asString() == AfterSuper::class.simpleName
                            || it.shortName.asString() == Before::class.simpleName
                            || it.shortName.asString() == BeforeSuper::class.simpleName
                }.iterator().forEach { annotation ->
                    val injectData = InjectorHookData(injectClass, injectMethod, injectMethodDescBuilder.toString())

                    annotation.arguments.forEach {
                        when (it.name?.asString()) {
                            "clazz" -> {
                                val clazz = it.value as KSType
                                injectData.targetClassName = clazz.declaration.getClassName()
                                logger.warn("targetClassName:${injectData.targetClassName}")
                            }
                            "method" -> {
                                val method = it.value as String
                                injectData.targetMethodName = method
                                logger.warn("targetMethodName:${method}")
                            }
                            "parameterTypes" -> {
                                val types = it.value as ArrayList<KSType>
                                val targetMethodDescBuilder = StringBuilder("(")
                                types.forEach {
                                    targetMethodDescBuilder.append(it.typeName(resolver))
                                }
                                targetMethodDescBuilder.append(")")
                                injectData.targetMethodDesc = targetMethodDescBuilder.toString()
                                logger.warn("parameterTypes:${targetMethodDescBuilder}")
                            }
                            "returnType" -> {
                                val returnType = it.value as KSType
                                injectData.targetMethodReturnDesc = returnType.typeName(resolver) ?: ""
                                logger.warn("targetMethodReturnDesc:${injectData.targetMethodReturnDesc}")
                            }
                        }
                    }
                    if (annotation.shortName.asString() == After::class.simpleName) {
                        injectData.isAfter = true
                        aroundHookClassArgs.add(injectData)
                    } else if (annotation.shortName.asString() == Before::class.simpleName) {
                        injectData.isAfter = false
                        aroundHookClassArgs.add(injectData)
                    } else if (annotation.shortName.asString() == AfterSuper::class.simpleName) {
                        injectData.isAfter = true
                        superHookClassArgs.add(injectData)
                    } else if (annotation.shortName.asString() == BeforeSuper::class.simpleName) {
                        injectData.isAfter = false
                        superHookClassArgs.add(injectData)
                    }
                }

                function.annotations.filter {
                    it.shortName.asString() == Inject::class.simpleName
                }.iterator().forEach { annotation ->
                    val injectData = InjectorHookData(injectClass)
                    var isSuper = false
                    annotation.arguments.forEach {
                        when (it.name?.asString()) {
                            "targetClazz" -> {
                                injectData.targetClassName = it.value as String
                                logger.warn("targetClassName:${injectData.targetClassName}")
                            }
                            "targetMethod" -> {
                                val method = it.value as String
                                injectData.targetMethodName = method
                                logger.warn("targetMethodName:${method}")
                            }
                            "targetMethodDesc" -> {
                                val typeDesc = it.value as String
                                injectData.targetMethodDesc = typeDesc
                                logger.warn("parameterTypes:${typeDesc}")
                            }
                            "injectMethod" -> {
                                val method = it.value as String
                                injectData.injectMethodName = method
                                logger.warn("targetMethodName:${method}")
                            }
                            "injectMethodDesc" -> {
                                val typeDesc = it.value as String
                                injectData.injectMethodDesc = typeDesc
                                logger.warn("parameterTypes:${typeDesc}")
                            }
                            "isSuper" -> {
                                isSuper = it.value as Boolean
                                logger.warn("isSuper:${isSuper}")
                            }
                            "isAfter" -> {
                                val isAfter = it.value as Boolean
                                injectData.isAfter = isAfter
                                logger.warn("isAfter:${isAfter}")
                            }
                        }
                    }
                    if (!isSuper) {
                        aroundHookClassArgs.add(injectData)
                    } else {
                        superHookClassArgs.add(injectData)
                    }

                }
            }
            super.visitClassDeclaration(classDeclaration, data)
        }
    }
}


