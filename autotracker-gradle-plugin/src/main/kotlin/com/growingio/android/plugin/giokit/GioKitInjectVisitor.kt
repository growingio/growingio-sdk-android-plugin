package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.ClassContextCompat
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * <p>
 *
 * @author cpacm 2023/9/06
 */
internal class GioKitInjectVisitor(
    api: Int,
    ncv: ClassVisitor,
    private val classContext: ClassContextCompat,
    private val giokitParams: GioKitParams
) : ClassVisitor(api, ncv), ClassContextCompat by classContext {

    override fun visitMethod(
        access: Int,
        methodName: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, methodName, descriptor, signature, exceptions)
        val data = GioKitInjectData.matchGioKitData(classContext.className, methodName, descriptor) ?: return mv
        return when (data) {
            GioKitInjectData.GioKitInjectInit -> {
                val config = hashMapOf<String, Any>()
                config["attach"] = giokitParams.autoAttachEnabled
                config["xmlScheme"] = giokitParams.xmlScheme
                config["gioDepend"] = giokitParams.dependLibs
                GioKitInitAdapter(api, mv, access, methodName, descriptor, config, data)
            }

            GioKitInjectData.GioKitInjectDatabaseInsert,
            GioKitInjectData.GioKitInjectDatabaseDeleteId,
            GioKitInjectData.GioKitInjectDatabaseRemove,
            GioKitInjectData.GioKitInjectDatabaseOverdue -> {
                GioKitDatabaseAdapter(api, mv, access, methodName, descriptor, data)
            }

            GioKitInjectData.GioKitInjectOkhttpV3,
            GioKitInjectData.GioKitInjectUrlConn,
            GioKitInjectData.GioKitInjectVolleySuccess,
            GioKitInjectData.GioKitInjectVolleyFail -> {
                GioKitNetworkAdapter(api, mv, access, methodName, descriptor, data)
            }

            else -> mv
        }
    }
}