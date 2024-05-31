package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.g
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * <p>
 *
 * @author cpacm 2023/9/8
 */
internal class GioKitNetworkAdapter(
    api: Int,
    nmv: MethodVisitor,
    access: Int,
    private val name: String?,
    private val descriptor: String?,
    private val injectData: GioKitInjectData
) : AdviceAdapter(api, nmv, access, name, descriptor) {


    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

        if (injectData is GioKitInjectData.GioKitInjectOkhttpV3) {
            if (opcode == Opcodes.INVOKEVIRTUAL &&
                owner == "okhttp3/OkHttpClient\$Builder" &&
                name == "addInterceptor" &&
                descriptor == "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient\$Builder;"
            ) {
                //add Okhttp3 GioHttpCaptureInterceptor
                g("hook OkHttpDataLoader succeed::${injectData.targetClassName}#${injectData.targetMethodName}")
                visitTypeInsn(Opcodes.NEW, injectData.injectClassName)
                visitInsn(Opcodes.DUP)
                super.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
                super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "okhttp3/OkHttpClient\$Builder",
                    "addNetworkInterceptor",
                    "(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient\$Builder;",
                    false
                )
            }
        }
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {

        if (injectData is GioKitInjectData.GioKitInjectUrlConn) {
            if (opcode == Opcodes.PUTFIELD &&
                owner == "com/growingio/android/urlconnection/UrlConnectionFetcher" &&
                name == "stream" &&
                descriptor == "Ljava/io/InputStream;"
            ) {
                g("hook UrlConnection succeed::${injectData.targetClassName}#${injectData.targetMethodName}")
                visitVarInsn(Opcodes.ALOAD, 0)
                super.visitFieldInsn(
                    Opcodes.GETFIELD,
                    "com/growingio/android/urlconnection/UrlConnectionFetcher",
                    "urlConnection",
                    "Ljava/net/HttpURLConnection;"
                )

                visitVarInsn(Opcodes.ALOAD, 4)
                visitVarInsn(Opcodes.ALOAD, 5)

                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }
        }

        super.visitFieldInsn(opcode, owner, name, descriptor)
    }


    override fun onMethodEnter() {
        super.onMethodEnter()

        when (injectData) {
            GioKitInjectData.GioKitInjectVolleySuccess -> {
                //deleteEventId
                g("hook volley succeed callback:${injectData.targetClassName}#${injectData.targetMethodName}")
                visitVarInsn(Opcodes.ALOAD, 0)
                visitVarInsn(Opcodes.ALOAD, 1)
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    injectData.injectClassName,
                    injectData.injectMethodName,
                    injectData.injectMethodDesc,
                    false
                )
            }

            GioKitInjectData.GioKitInjectVolleyFail -> {
                g("hook volley error callback:${injectData.targetClassName}#${injectData.targetMethodName}")
                //GioDatabase.deleteEvent
                visitVarInsn(Opcodes.ALOAD, 0)
                visitVarInsn(Opcodes.ALOAD, 1)
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