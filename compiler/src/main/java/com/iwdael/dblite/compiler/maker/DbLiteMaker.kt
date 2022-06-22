package com.iwdael.dblite.compiler.maker

import com.iwdael.dblite.compiler.DTA
import com.iwdael.dblite.compiler.compat.TAB
import com.iwdael.dblite.compiler.compat.firstLetterLowercase


class DbLiteMaker(private val entities: List<DTA>) : Maker {

    companion object {
        private const val REPLACE_ENTITY = "#REPLACE_ENTITY#"
        private const val REPLACE_VERSION = "#REPLACE_VERSION#"
        private const val REPLACE_ROOM_2 = "#REPLACE_ROOM_2#"
        private const val REPLACE_ROOM = "#REPLACE_ROOM#"
        private const val REPLACE_IMPORT = "#REPLACE_IMPORT#"
        private const val CONTENT =
            "" +
                    "package com.iwdael.dblite\n" +
                    "\n" +
                    "import android.content.Context\n" +
                    "import androidx.room.Database\n" +
                    "import androidx.room.Room\n" +
                    "import androidx.room.RoomDatabase\n" +
                    "\n" +
                    "$REPLACE_IMPORT" +
                    "\n" +
                    "/**\n" +
                    " * author : iwdael\n" +
                    " * e-mail : iwdael@outlook.com\n" +
                    " */\n" +
                    "@Database(entities = [$REPLACE_ENTITY], version = $REPLACE_VERSION)\n" +
                    "abstract class DbLite : RoomDatabase() {\n" +
                    "\n" +
                    "$REPLACE_ROOM" +
                    "\n" +
                    "${TAB}companion object {\n" +
                    "$TAB$TAB@Volatile\n" +
                    "$TAB${TAB}private var instance: DbLite? = null\n" +
                    "\n" +
                    "$TAB${TAB}fun instance(): DbLite {\n" +
                    "$TAB$TAB${TAB}if (instance == null) throw Exception(\"Please initialize DbLite first\")\n" +
                    "$TAB$TAB${TAB}return instance!!\n" +
                    "$TAB$TAB}\n" +
                    "\n" +
                    "$TAB${TAB}fun init(context: Context) {\n" +
                    "$TAB$TAB${TAB}if (instance != null) return\n" +
                    "$TAB$TAB${TAB}synchronized(DbLite::class.java) {\n" +
                    "$TAB$TAB$TAB${TAB}if (instance != null) return\n" +
                    "$TAB$TAB$TAB${TAB}instance = Room.databaseBuilder(context, DbLite::class.java, \"dblite.db\").build()\n" +
                    "$TAB$TAB$TAB}\n" +
                    "$TAB$TAB}\n" +
                    "\n" +
                    "$REPLACE_ROOM_2" +
                    "\n" +
                    "$TAB}\n" +
                    "\n" +
                    "}"
    }

    override fun classFull() = "com.iwdael.dblite.DbLite"
    override fun className() = "DbLite"
    override fun packageName() = "com.iwdael.dblite"
    override fun make(): String {
        return CONTENT
            .replace(
                REPLACE_IMPORT,
                entities.map { "import ${it.packageName}.${it.targetClassName}" }
                    .joinToString(separator = "\n")
            )
            .replace(
                REPLACE_ENTITY,
                entities.joinToString(
                    separator = ",",
                    transform = { "${it.targetClassName}::class" })
            )
            .replace(REPLACE_VERSION, "1")
            .replace(
                REPLACE_ROOM, entities.map { it.targetClassName }
                .joinToString(
                    separator = "\n",
                    transform = { "    abstract fun ${it.firstLetterLowercase()}(): ${it}Room" },
                    postfix = "\n"
                )
            )
            .replace(
                REPLACE_ROOM_2, entities.map { it.targetClassName }
                .joinToString(
                    separator = "\n",
                    transform = { "        fun ${it.firstLetterLowercase()}() = instance().${it.firstLetterLowercase()}()" },
                    postfix = "\n"
                )
            )
    }


}