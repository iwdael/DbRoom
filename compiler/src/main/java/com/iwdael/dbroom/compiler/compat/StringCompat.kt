package com.iwdael.dbroom.compiler.compat

import java.util.Locale

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */

fun String.smallHump(): String {
    return "${this.substring(0, 1).lowercase(Locale.ROOT)}${this.substring(1)}"
}
fun String.bigHump(): String {
    return "${this.substring(0, 1).uppercase(Locale.ROOT)}${this.substring(1)}"
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