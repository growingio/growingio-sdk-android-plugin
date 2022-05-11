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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*


/**
 * <p>
 *
 * @author cpacm 2022/5/9
 */

fun normalize(type: String) = if (type.contains('/')) {
    type.replace('/', '.')
} else {
    type
}

fun String.unNormalize(): String {
    return if (this.contains('.')) {
        this.replace('.', '/')
    } else {
        this
    }
}

fun String.innerClass(): String {
    return if (this.contains('.')) {
        this.replace('.', '$')
    } else {
        this
    }
}

/**
 * Turns a KSTypeReference into a TypeName in java's type system.
 */
internal fun KSTypeReference?.typeName(resolver: Resolver): String? {
    return if (this == null) {
        null
    } else {
        resolve().typeName(resolver)
    }
}

/**
 * Turns a KSTypeArgument into a TypeName in java's type system.
 */
internal fun KSTypeArgument.typeName(
    resolver: Resolver
): String? {
    return type.typeName(resolver)
}

/**
 * Turns a KSType into a TypeName in java's type system.
 */
internal fun KSType.typeName(resolver: Resolver): String? {
    if (this.arguments.isNotEmpty() && this.arguments.size == 1) { //for array
        val ksTypeArg = this.arguments.single()
        return when (ksTypeArg.variance) {
            // 泛型统一使用自身类型 包括 out,in,* 三种
            Variance.CONTRAVARIANT -> this.declaration.typeName(resolver)
            Variance.COVARIANT -> this.declaration.typeName(resolver)
            Variance.STAR -> {
                // for star projected types, JavaPoet uses the name from the declaration if
                // * is not given explicitly
                this.declaration.typeName(resolver)
            }
            //数组
            else -> "[" + ksTypeArg.typeName(resolver)
        }
    } else {
        //map
        return this.declaration.typeName(resolver)
    }
}

/**
 * Turns a KSDeclaration into a TypeName in java's type system.
 */
@OptIn(KspExperimental::class)
internal fun KSDeclaration.typeName(resolver: Resolver): String? {
    val jvmSignature = resolver.mapToJvmSignature(this)
    if (jvmSignature?.isNotBlank() == true) {
        return KspTypeMapper.getJavaTypeName(jvmSignature)
    }

    // fallback to custom generation, it is very likely that this is an unresolved type
    // get the package name first, it might throw for invalid types, hence we use
    // safeGetPackageName
    val qualified = qualifiedName?.asString() ?: return null
    val pkg = getNormalizedPackageName().unNormalize()
    val shortNames = if (pkg == "") {
        qualified
    } else {
        qualified.substring(pkg.length + 1)
    }
    return "L$pkg/$shortNames;"
}

internal fun KSDeclaration.getClassName(): String {
    val qualified = qualifiedName?.asString() ?: return simpleName.asString()
    val pkg = getNormalizedPackageName().unNormalize()
    val shortNames = if (pkg == "") {
        qualified
    } else {
        qualified.substring(pkg.length + 1).innerClass()
    }
    return "$pkg/$shortNames"
}

/**
 * Root package comes as <root> instead of "" so we work around it here.
 */
internal fun KSDeclaration.getNormalizedPackageName(): String {
    return packageName.asString().let {
        if (it == "<root>") {
            ""
        } else {
            it
        }
    }
}

object KspTypeMapper {
    private val K2JMapping = mutableMapOf<String, String>()

    init {
        K2JMapping["Lkotlin/Unit;"] = "V"
    }

    fun getJavaTypeName(kotlinType: String) = K2JMapping[kotlinType] ?: kotlinType
}
