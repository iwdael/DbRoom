package com.iwdael.dbroom.compiler.compat

import java.io.File

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
fun File.searchPathWithEnd(path: String, vararg filters: String): String? {
    if (isFile) return null
    val list = listFiles()
    for (file in list) {
        if (file.absolutePath.endsWith(path)) {
            var f = false
            for (filter in filters) {
                if (!file.absolutePath.contains(filter)) {
                    f = true
                }
            }
            if (filters.isEmpty()) f = true
            if (f) return file.absolutePath
        }
        val path = file.searchPathWithEnd(path)
        if (path != null) {
            var f = false
            for (filter in filters) {
                if (!path.contains(filter)) {
                    f = true
                }
            }
            if (filters.isEmpty()) f = true
            if (f) return path
        }
    }
    return null
}