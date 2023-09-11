package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.g
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * <p>
 *
 * @author cpacm 2023/9/7
 */
internal class GioKitInitAdapter(
    api: Int,
    nmv: MethodVisitor,
    access: Int,
    private val name: String?,
    private val descriptor: String?,
    private val config: Map<String, Any>,
    private val injectData: GioKitInjectData
) : AdviceAdapter(api, nmv, access, name, descriptor) {

    lateinit var localVariables: IntArray

    override fun onMethodEnter() {
        val targetArgs: Array<Type> = Type.getArgumentTypes(descriptor)
        localVariables = IntArray(targetArgs.size + 1)

        for (i in targetArgs.indices) {
            loadArg(i)
            localVariables[i + 1] = newLocal(targetArgs[i])
            storeLocal(localVariables[i + 1])
        }

        super.onMethodEnter()
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        val attach = config.getOrElse("attach") { true } as Boolean
        val xmlScheme = config.getOrElse("xmlScheme") { "" } as String
        val dependStr = config.getOrElse("gioDepend") { "" } as String

        g("GioKit init after SDK setup.")
        //new HashMap
        visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
        visitInsn(Opcodes.DUP)
        visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false)
        //保存变量
        visitVarInsn(Opcodes.ASTORE, 2)
        //put("attach",true)
        visitVarInsn(Opcodes.ALOAD, 2)
        visitLdcInsn("attach")
        visitInsn(if (attach) Opcodes.ICONST_1 else Opcodes.ICONST_0)
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/Boolean",
            "valueOf",
            "(Z)Ljava/lang/Boolean;",
            false
        )
        visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            "java/util/Map",
            "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            true
        )

        visitInsn(Opcodes.POP)

        //put("xmlScheme","")
        visitVarInsn(Opcodes.ALOAD, 2)
        visitLdcInsn("xmlScheme")
        visitLdcInsn(xmlScheme)
        visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            "java/util/Map",
            "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            false

        )
        visitInsn(Opcodes.POP)

        //put("gioDepend","")
        visitVarInsn(Opcodes.ALOAD, 2)
        visitLdcInsn("gioDepend")
        visitLdcInsn(dependStr)
        visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            "java/util/Map",
            "put",
            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            false
        )
        visitInsn(Opcodes.POP)

        //GioPluginConfig.inject(context: Context, config: Map<String, Any>)
        //visitVarInsn(Opcodes.ALOAD, 1)
        loadLocal(localVariables[1])
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/growingio/android/sdk/TrackerContext",
            "getBaseContext",
            "()Landroid/content/Context;",
            false
        )

        visitVarInsn(Opcodes.ALOAD, 2)
        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            injectData.injectClassName,
            injectData.injectMethodName,
            injectData.injectMethodDesc,
            false
        )
    }
}