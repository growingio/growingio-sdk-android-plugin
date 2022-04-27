package com.growingio.android.plugin.utils

import java.io.Closeable
import java.io.File
import java.net.URLClassLoader

/**
 * <p>
 *
 * @author cpacm 2021/12/10
 */
abstract class AbstractKlassPool(private val classpath: Collection<File>, final override val parent: KlassPool? = null) :
    KlassPool {

    private val classes = mutableMapOf<String, Klass>()

    protected val imports = mutableMapOf<String, Collection<String>>()

    override val classLoader: ClassLoader = URLClassLoader(classpath.map { it.toURI().toURL() }.toTypedArray(), parent?.classLoader)

    override operator fun get(type: String) = normalize(type).let { name ->
        classes.getOrDefault(name, findClass(name))
    }

    override fun close() {
        val classLoader = this.classLoader
        if (classLoader is URLClassLoader) {
            classLoader.close()
        }
    }

    override fun toString() = "classpath: $classpath"

    internal fun getImports(name: String): Collection<String> = this.imports[name] ?: this.parent?.let { it ->
        if (it is AbstractKlassPool) it.getImports(name) else null
    } ?: emptyList()

    internal fun findClass(name: String): Klass {
        return try {
            LoadedKlass(this, classLoader.loadClass(name)).also {
                classes[name] = it
            }
        } catch (e: Throwable) {
            DefaultKlass(name)
        }
    }

}

private class DefaultKlass(name: String) : Klass {

    override val qualifiedName: String = name

    override fun isAssignableFrom(type: String) = false

    override fun isAssignableFrom(klass: Klass) = klass.qualifiedName == this.qualifiedName

}

private class LoadedKlass(val pool: AbstractKlassPool, val clazz: Class<out Any>) : Klass {

    override val qualifiedName: String = clazz.name

    override fun isAssignableFrom(type: String) = isAssignableFrom(pool.findClass(normalize(type)))

    override fun isAssignableFrom(klass: Klass) = klass is LoadedKlass && clazz.isAssignableFrom(klass.clazz)

}

interface Klass {

    /**
     * The qualified name of class
     */
    val qualifiedName: String

    /**
     * Tests if this class is assignable from the specific type
     *
     * @param type the qualified name of type
     */
    fun isAssignableFrom(type: String): Boolean

    /**
     * Tests if this class is assignable from the specific type
     *
     * @param klass the [Klass] object to be checked
     */
    fun isAssignableFrom(klass: Klass): Boolean

}

interface KlassPool : Closeable {

    /**
     * Returns the parent
     */
    val parent: KlassPool?

    /**
     * Returns the class loader
     */
    val classLoader: ClassLoader

    /**
     * Returns an instance [Klass]
     *
     * @param type the qualified name of class
     */
    operator fun get(type: String): Klass
}
