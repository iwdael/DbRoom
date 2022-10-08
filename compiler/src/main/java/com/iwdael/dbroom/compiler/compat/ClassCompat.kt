package com.iwdael.dbroom.compiler.compat

import com.iwdael.annotationprocessorparser.Class
import java.io.File

fun Class.filePath(): String? {
    val sourceFile = this.e.javaClass.getDeclaredField("sourcefile")
        .apply { isAccessible = true }
        .get(this.e) ?: return null
    val userPath = sourceFile.javaClass.getDeclaredField("userPath")
        .apply { isAccessible = true }
        .get(sourceFile) ?: return null;
    val path = "$userPath"
    if (e.getAnnotation(Metadata::class.java) == null) return path

    val build = File.separator + "build" + File.separator
    val debug = File.separator + "debug" + File.separator
    val release = File.separator + "release" + File.separator
    val i = path.indexOf(build)
    val tempJavaPath = path.substring(0, i) +
            "${File.separator}src${File.separator}main${File.separator}java${File.separator}" +
            path.substring(
                (path.indexOf(release, i) + release.length)
                    .coerceAtLeast(path.indexOf(debug, i) + debug.length)
            )
    val javaPath = tempJavaPath.replace(".java", ".kt")
    if (File(javaPath).exists()) return javaPath
    val rootDir = File(path.substring(0, i))
    return rootDir.searchPathWithEnd(
        path.substring(
            (path.indexOf(release, i) + release.length)
                .coerceAtLeast(path.indexOf(debug, i) + debug.length)
        ).replace(".java", ".kt"),
        build
    )
}