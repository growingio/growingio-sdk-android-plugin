package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.g
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * <p>
 *
 * @author cpacm 2023/9/8
 */
internal class GioKitDatabaseAdapter(
    api: Int,
    nmv: MethodVisitor,
    access: Int,
    private val name: String?,
    private val descriptor: String?,
    private val injectData: GioKitInjectData
) : AdviceAdapter(api, nmv, access, name, descriptor) {

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        if (injectData is GioKitInjectData.GioKitInjectDatabaseInsert) {
            if (opcode == Opcodes.GOTO) {
                //db insert
                g("db insert succeed:${injectData.targetClassName}#${injectData.targetMethodName}")
                visitVarInsn(Opcodes.ALOAD, 5)
                visitVarInsn(Opcodes.ALOAD, 4)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }
        }
        super.visitJumpInsn(opcode, label)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()

        when (injectData) {
            GioKitInjectData.GioKitInjectDatabaseDeleteId -> {
                //deleteEventId
                g("db deleteId:${injectData.targetClassName}#${injectData.targetMethodName}")
                visitVarInsn(Opcodes.LLOAD, 2)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }

            GioKitInjectData.GioKitInjectDatabaseUpdate -> {
                //delay events
                g("db update:${injectData.targetClassName}#${injectData.targetMethodName}")
                visitVarInsn(Opcodes.LLOAD, 1)
                visitVarInsn(Opcodes.ALOAD, 3)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }

            GioKitInjectData.GioKitInjectDatabaseRemove -> {
                g("db remove:${injectData.targetClassName}#${injectData.targetMethodName}")
                //GioDatabase.deleteEvent
                visitVarInsn(Opcodes.LLOAD, 1)
                visitVarInsn(Opcodes.ALOAD, 4)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }

            GioKitInjectData.GioKitInjectDatabaseOverdue -> {
                g("db overdue:${injectData.targetClassName}#${injectData.targetMethodName}")
                //GioDatabase.outdatedEvents
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }


            else -> {}
        }
    }
}