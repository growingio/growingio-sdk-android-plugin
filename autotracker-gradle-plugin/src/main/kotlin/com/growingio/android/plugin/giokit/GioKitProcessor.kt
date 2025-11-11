package com.growingio.android.plugin.giokit

import com.android.build.api.dsl.ApplicationExtension
import com.growingio.android.plugin.AutoTrackerExtension
import com.growingio.android.plugin.giokit.GioKitInjectData.Companion.DEFAULT_AUTOTRACKER_CALLED_METHOD
import com.growingio.android.plugin.util.AndroidManifestHandler
import com.growingio.android.plugin.util.asIterable
import com.growingio.android.plugin.util.g
import com.growingio.android.plugin.util.w
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import java.io.File
import java.lang.StringBuilder
import javax.xml.parsers.SAXParserFactory

/**
 * <p>
 *
 * @author cpacm 2023/9/6
 */
object GioKitProcessor {

    fun getDefaultFindDomains(trackerFinderDomains: Array<String>, applicationId: String): HashSet<String> {
        return if (trackerFinderDomains.isEmpty()) hashSetOf(applicationId) else {
            val domains = hashSetOf<String>()
            domains.addAll(trackerFinderDomains.iterator().asIterable())
            domains
        }
    }

    fun getDefaultCalledMethods(trackerCalledMethod: Array<String>): HashSet<String> {
        return if (trackerCalledMethod.isEmpty()) DEFAULT_AUTOTRACKER_CALLED_METHOD else {
            val methods = hashSetOf<String>()
            methods.addAll(trackerCalledMethod.iterator().asIterable())
            methods.addAll(DEFAULT_AUTOTRACKER_CALLED_METHOD)
            methods
        }
    }

    fun shouldClassModified(className: String): Boolean {
        return GioKitInjectData.shouldClassModified(className)
    }

    internal fun processGiokitParams(project: Project, gioExtension: AutoTrackerExtension): GioKitParams {
        return if (checkGiokitEnabled(project, gioExtension)) {
            val scheme = parseAndroidManifest(project)
            val dependLibs = getGioDepends(project)
            val gioKitExtension = gioExtension.giokit ?: GioKitExtension()
            val trackerFinderEnabled = gioKitExtension.trackerFinderEnabled
            val trackerFinderDomain: Array<String> = gioKitExtension.trackerFinderDomain ?: arrayOf()
            val trackerCalledMethod: Array<String> = gioKitExtension.trackerCalledMethod ?: arrayOf()
            val autoAttachEnabled: Boolean = gioKitExtension.autoAttachEnabled
            GioKitParams(
                true,
                scheme,
                dependLibs,
                trackerFinderEnabled,
                trackerFinderDomain,
                trackerCalledMethod,
                autoAttachEnabled,
            )
        } else {
            GioKitParams(false)
        }
    }

    fun checkGiokitEnabled(project: Project, gioExtension: AutoTrackerExtension): Boolean {
        val giokitExtension = gioExtension.giokit ?: return false
        if (!giokitExtension.enabled) return false
        if (isReleaseTask(project)) {
            return giokitExtension.releaseEnabled
        }
        return true
    }

    fun getGioDepends(project: Project): String {
        val gioSdks = mutableSetOf<Pair<String, String>>()
        val allSet = getGrowingDepends(project)
        for (dependLib in allSet) {
            if (dependLib.first.contains("com.growingio.android")
                || dependLib.first.contains(":growingio")
                || dependLib.first.contains(":gio-sdk")
                || dependLib.first.contains("com.growingio.giokit")
                || dependLib.first.contains(":giokit")
            ) {
                gioSdks.add(dependLib)
                g("add gio dependLib:${dependLib}")
            }
        }
        return getGioDepend(gioSdks)
    }

    private fun parseAndroidManifest(project: Project): String {
        var xmlScheme = ""
        val appExtension = project.extensions.getByName("android") as? ApplicationExtension
        appExtension?.let {
            val manifest = it.sourceSets.getAt("main").manifest
            manifest.let {
                val manifestFile = File(project.projectDir, manifest.toString())
                if (!manifestFile.exists()) {
                    w("can't find AndroidManifest.xml")
                    return xmlScheme
                }
                val parser = SAXParserFactory.newInstance().newSAXParser()
                val handler = AndroidManifestHandler()
                parser.parse(manifestFile, handler)
                xmlScheme = handler.growingioScheme ?: ""
                g("growingio xmlScheme is ${xmlScheme}")
                g("app xmlPackage is ${handler.appPackageName}")
            }
        }
        return xmlScheme
    }

    fun createGiokitSourceSets(project: Project, path: String) {
        val appExtension = project.extensions.getByName("android") as? ApplicationExtension
        appExtension?.let { extension ->
            extension.sourceSets.forEach {sourceSet->
                if (sourceSet.name == "main") {
                    sourceSet.java.srcDir(path)
                }
            }
        }
    }

    private fun getGioDepend(sdks: Set<Pair<String, String>>): String {
        val sb = StringBuilder()
        sdks.forEach { dependLib ->
            sb.append(dependLib.first).append(":").append(dependLib.second)
            if (sdks.last() != dependLib) {
                sb.append("##")
            }
        }
        return sb.toString()
    }

    private fun getGrowingDepends(project: Project): Set<Pair<String, String>> {
        if (project.state.failure != null) {
            return mutableSetOf()
        }
        val allSet = mutableSetOf<Pair<String, String>>()
        project.configurations.flatMap { configuration ->
            configuration.dependencies.map { dependency ->
                if (dependency is DefaultExternalModuleDependency) {
                    allSet.add(
                        Pair(
                            dependency.group + ":" + dependency.name,
                            dependency.version ?: "undefined"
                        )
                    )
                } else {
                    allSet.add(Pair(":" + dependency.name, "project"))
                }
                dependency.group to dependency.name
            }
        }
        return allSet
    }

    fun autoInstallGioKit(project: Project, releaseEnabled: Boolean = false, autoInstallVersion: String? = null) {
        var gioKitVersion: String? = null
        val gioDepends = getGrowingDepends(project)
        gioDepends.forEach { (depend, version) ->
            if (depend == GIOKIT_DEPEND || depend == ":giokit") {
                gioKitVersion = version
                return@forEach
            }
        }
        if (gioKitVersion != null) {
            g("giokit was already installed directly")
            return
        }
        gioKitVersion = autoInstallVersion ?: GIOKIT_DEFAULT_VERSION
        if (releaseEnabled) {
            project.configurations.named("implementation").configure { configuration ->
                configuration.withDependencies {
                    val depend = project.dependencies.create("$GIOKIT_DEPEND:$gioKitVersion")
                    it.add(depend)
                    g("giokit was successfully installed with version: $gioKitVersion")
                }
            }
        } else {
            project.configurations.named("debugImplementation").configure { configuration ->
                configuration.withDependencies {
                    val depend = project.dependencies.create("$GIOKIT_DEPEND:$gioKitVersion")
                    it.add(depend)
                }
            }

            project.configurations.named("releaseImplementation").configure { configuration ->
                configuration.withDependencies {
                    val depend = project.dependencies.create("$GIOKIT_DEPEND_NOOP:$gioKitVersion")
                    it.add(depend)
                }
            }
            g("giokit was successfully installed with version: $gioKitVersion")
        }
    }

    private fun isReleaseTask(project: Project): Boolean {
        return project.gradle.startParameter.taskNames.any {
            it.lowercase().contains("release")
        }
    }

    fun getGeneratedDir(buildDir: File, name: String): File {
        return File(buildDir, "generated/source/giokit/$name/")
    }

    fun getVisitorCodeFile(buildDir: File): File {
        return File(buildDir.absolutePath + File.separator + "tmp", "giokit_track_scan.txt")
    }

    private const val GIOKIT_DEPEND = "com.growingio.giokit:giokit"
    private const val GIOKIT_DEPEND_NOOP = "com.growingio.giokit:giokit-no-op"
    private const val GIOKIT_DEFAULT_VERSION = "2.1.4"
}