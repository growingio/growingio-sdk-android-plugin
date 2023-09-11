package com.growingio.android.plugin.giokit

import com.growingio.android.plugin.util.ClassContextCompat
import com.growingio.android.plugin.util.g
import com.growingio.android.plugin.util.normalize
import com.growingio.android.plugin.util.unNormalize
import com.growingio.android.plugin.util.w
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import java.io.*
import javax.lang.model.element.Modifier


/**
 * <p>
 *
 * @author cpacm 2022/6/16
 */
internal class GioKitCodeVisitor(
    api: Int,
    ncv: ClassVisitor,
    private val context: ClassContextCompat,
    private val trackerCalledMethods: HashSet<String>,
    private val generatedDir: File,
    private val tempDir: File
) : ClassVisitor(api, ncv) {

    private val calledMethods: HashSet<Pair<String, String>> = hashSetOf()

    private val generatedMethods = hashSetOf<String>()

    init {
        val configMethods = hashSetOf<String>()
        trackerCalledMethods.forEach {
            configMethods.add(it)
        }

        configMethods.forEach { classMethod ->
            val splitArray = classMethod.split("#")
            if (splitArray.size == 2) {
                calledMethods.add(Pair(splitArray[0], splitArray[1]))
            } else {
                w("trackerCalledMethod error with $classMethod")
            }
        }
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodFindVisitor(api, mv, access, name, descriptor)
    }

    override fun visitEnd() {
        if (generatedMethods.isEmpty()) return super.visitEnd()
        generateCode(hookGenerateCode())
        super.visitEnd()
    }

    fun generateCode(codeSet: Set<String>) {
        generatedDir.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        g("creating a class with public modifier and writing it to genDir-${generatedDir.canonicalPath}")

        // creating a class DlkanthDemo with public modifier and writing it to genDir
        val classBuilder = TypeSpec.classBuilder("GioCode").superclass(
            ParameterizedTypeName.get(
                HashSet::class.java,
                String::class.java
            )
        )
        classBuilder.addModifiers(Modifier.PUBLIC)
        val builder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        codeSet.forEach {
            val code = "add(\"${it.replace("$", "#")}\")"
            builder.addStatement(code)
        }

        classBuilder.addMethod(
            builder.build()
        )

        val javaFile = JavaFile.builder("com.growingio.giokit", classBuilder.build()).build()
        javaFile.writeTo(generatedDir)
    }

    // 在增量编译的情况下，无法解决->文件删除时立马更新的问题
    private fun hookGenerateCode(): HashSet<String> {
        val codeSet = hashSetOf<String>()
        try {
            val file = tempDir
            if (!file.exists()) {
                file.createNewFile()
            } else {
                val fr = FileReader(file)
                val br = BufferedReader(fr)
                br.lineSequence().forEach {
                    codeSet.add(it)
                }
            }
            codeSet.removeIf {
                it.startsWith(context.className + "::")
            }
            generatedMethods.forEach {
                codeSet.add(it)
            }
            val fw = FileWriter(file, false)
            val bw = BufferedWriter(fw)
            codeSet.forEach {
                bw.write(it)
                bw.newLine()
            }
            bw.flush()
            bw.close()
            fw.close()
        } catch (e: IOException) {
            w(e.message ?: "can't generate code file")
        }
        return codeSet
    }

    inner class MethodFindVisitor(
        api: Int,
        nmv: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
    ) : AdviceAdapter(api, nmv, access, name, descriptor) {

        private var index = 1
        override fun visitMethodInsn(
            opcodeAndSource: Int,
            owner2: String?,
            name2: String?,
            descriptor: String?,
            isInterface: Boolean
        ) {
            val gName = name.let {
                if (index > 1) "$it-$index"
                else it
            }
            calledMethods.filter { owner2 == it.first.unNormalize() && name2 == it.second }.forEach { _ ->
                g("find ${context.className} :: ${name}")
                generatedMethods.add("${context.className}::${gName}")
                g("[generate index]:$index:${context.className} :: ${name}")
                index += 1
            }

            super.visitMethodInsn(opcodeAndSource, owner2, name2, descriptor, isInterface)
        }
    }
}