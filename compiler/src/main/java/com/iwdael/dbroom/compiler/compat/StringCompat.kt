package com.iwdael.dbroom.compiler.compat

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun String.firstLetterLowercase(): String {
    return "${this.substring(0, 1).toLowerCase()}${this.substring(1)}"
}
fun String.firstLetterUppercase(): String {
    return "${this.substring(0, 1).toUpperCase()}${this.substring(1)}"
}

fun String.kotlin(): String {
    return when {
        this == "java.lang.String" -> "kotlin.String"
        this == "java.lang.String[]" -> "kotlin.Array<String>"
        this == "int" -> "kotlin.Int"
        this == "int[]" -> "kotlin.IntArray"
        this == "boolean" -> "kotlin.Boolean"
        this == "boolean[]" -> "kotlin.BooleanArray"
        this == "float" -> "kotlin.Float"
        this == "float[]" -> "kotlin.FloatArray"
        this == "long" -> "kotlin.Long"
        this == "long[]" -> "kotlin.LongArray"
        this == "double" -> "kotlin.Double"
        this == "double[]" -> "kotlin.DoubleArray"
        this == "short" -> "kotlin.Short"
        this == "short[]" -> "kotlin.ShortArray"
        this == "byte" -> "kotlin.Byte"
        this == "byte[]" -> "kotlin.ByteArray"
        this == "char" -> "kotlin.Char"
        this == "char[]" -> "kotlin.CharArray"
        this == "java.util.List" -> "kotlin.collections.List"
        this == "java.lang.Integer" -> "kotlin.Int"
        else -> this
    }
}
