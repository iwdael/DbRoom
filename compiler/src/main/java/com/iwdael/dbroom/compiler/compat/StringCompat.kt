package com.iwdael.dbroom.compiler.compat

import com.iwdael.dbroom.compiler.JavaClass.MASTER_PACKAGE
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.annotation.processing.RoundEnvironment

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
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


const val FILE_COMMENT =
    "Create by https://github.com/iwdael/dbroom"

const val TYPE_COMMENT =
    "@author  : iwdael\n" +
            "@mail    : iwdael@outlook.com\n" +
            "@project : https://github.com/iwdael/dbroom"


const val CREATOR_EXAMPLE = "\n" +
        "kotlin example:\n\n" +
        "\nfun <T : RoomDatabase> create(context: Context, room: Class<T>): T {\n" +
        "    return Room.databaseBuilder(context, room, \"DbRoom.db\").build()\n" +
        "}\n" +
        "\n\n" +
        "java example:\n\n" +
        "public static <T extends RoomDatabase> T createRoomDatabase(Context context, Class<T> room) {\n" +
        "    return Room.databaseBuilder(context, room, \"DbRoom.db\").build();\n" +
        "}" +
        "\n\n"