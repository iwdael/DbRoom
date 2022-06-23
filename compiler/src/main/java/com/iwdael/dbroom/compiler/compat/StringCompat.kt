package com.iwdael.dbroom.compiler.compat


fun String.firstLetterLowercase(): String {
    return "${this.substring(0, 1).toLowerCase()}${this.substring(1)}"
}

fun String.javaFullClass2KotlinShotClass(): String {
    return when {
        this == "java.lang.String" -> "String"
        this == "java.lang.String[]" -> "Array<String>"
        this == "int" -> "Int"
        this == "int[]" -> "IntArray"
        this == "boolean" -> "Boolean"
        this == "boolean[]" -> "BooleanArray"
        this == "float" -> "Float"
        this == "float[]" -> "FloatArray"
        this == "long" -> "Long"
        this == "long[]" -> "LongArray"
        this == "double" -> "Double"
        this == "double[]" -> "DoubleArray"
        this == "short" -> "Short"
        this == "short[]" -> "ShortArray"
        this == "byte" -> "Byte"
        this == "byte[]" -> "ByteArray"
        this == "char" -> "Char"
        this == "char[]" -> "CharArray"
        this.endsWith("[]") -> "Array<${this.replace("[]", "")}>".javaFullClass2KotlinShotClass()
        this.contains("java.util.") -> this.replace("java.util.", "")
            .javaFullClass2KotlinShotClass()
        this.contains("java.lang.") -> this.replace("java.lang.", "")
            .javaFullClass2KotlinShotClass()
        this.contains("Integer") -> this.replace("Integer", "Int").javaFullClass2KotlinShotClass()
        else -> this
    }
}

const val TAB = "    "