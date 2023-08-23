package com.growingio.android.plugin.visitor

import com.growingio.android.plugin.util.ClassContextCompat
import com.growingio.android.plugin.util.info
import com.growingio.android.plugin.util.normalize
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.GeneratorAdapter

/**
 * <p>
 *
 * @author cpacm 2023/8/22
 */
class SaasConfigClassVisitor(api: Int, ncv: ClassVisitor, private val classContext: ClassContextCompat) :
    ClassVisitor(api, ncv), ClassContextCompat by classContext {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (normalize(classContext.className) == "com.growingio.android.sdk.collection.GConfig") {
            if (name.equals("isInstrumented")) {
                info("[visitMethod] ${classContext.className} with $name")
                val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
                return BooleanMethodVisitor(api, mv, access, name, descriptor)
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    internal class BooleanMethodVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) : GeneratorAdapter(api, nmv, access, name, descriptor) {

        override fun visitCode() {
            super.visitInsn(Opcodes.ICONST_1)
            super.visitInsn(Opcodes.IRETURN)
        }
    }
}