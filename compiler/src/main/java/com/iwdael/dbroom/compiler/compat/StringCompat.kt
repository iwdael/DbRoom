package com.iwdael.dbroom.compiler.compat

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.lang.StringBuilder

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun String.charLower(): String {
    return "${this.substring(0, 1).toLowerCase()}${this.substring(1)}"
}

fun String.charUpper(): String {
    return "${this.substring(0, 1).toUpperCase()}${this.substring(1)}"
}


fun String.bestGuessClassName(): TypeName {
    return try {
        ClassName.bestGuess(this)
    } catch (e: Exception) {
        when (this) {
            "boolean" -> TypeName.BOOLEAN
            "byte" -> TypeName.BYTE
            "short" -> TypeName.SHORT
            "int" -> TypeName.INT
            "long" -> TypeName.LONG
            "char" -> TypeName.CHAR
            "float" -> TypeName.FLOAT
            "double" -> TypeName.DOUBLE
            else -> ClassName.bestGuess(this)
        }
    }
}

fun String.simpleClassName(): String {
    val last = this.lastIndexOf(".")
    if (last == -1) return this
    return this.substring(last + 1)
}

fun StringBuilder.setterLastLineIndex(clazz: String, methodName: String): Int {
    var i = this.indexOf(methodName)
    var index = i
    if (index == -1) return -1
    val count = this.length
    index += methodName.length
    while (index < count) {
        val i1 = this.indexOf("(", index)
        if (i1 == -1) return -1
        if (i1 == -1) continue
        index = i1 + 1
        val i2 = this.indexOf(")", index)
        if (i2 == -1) continue
        val parameter = this.substring(index, i2).trim()
        index = i2
        if (!parameter.contains(clazz) || parameter.contains(",")) continue
        val i3 = this.indexOf("}", index)
        if (i3 == -1) continue
        return i3
    }
    return -1

}