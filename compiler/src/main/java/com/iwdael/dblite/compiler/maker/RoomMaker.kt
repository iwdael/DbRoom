package com.iwdael.dblite.compiler.maker

import com.iwdael.dblite.compiler.DTA
import com.iwdael.dblite.compiler.compat.javaFullClass2KotlinShotClass


class RoomMaker(private val DTA: DTA) : Maker {

    companion object {
        private const val REPLACE_FIND_ALL = "#REPLACE_FIND_ALL#"
        private const val REPLACE_FIND = "#REPLACE_FIND#"
        private const val REPLACE_ENTITY = "#REPLACE_ENTITY#"
        private const val REPLACE_CLASS_NAME = "#REPLACE_CLASS_NAME#"
        private const val CONTENT =
            "" +
                    "package com.iwdael.dblite\n" +
                    "\n" +
                    "import androidx.room.Dao\n" +
                    "import androidx.room.Delete\n" +
                    "import androidx.room.Insert\n" +
                    "import androidx.room.Query\n" +
                    "import $REPLACE_ENTITY\n" +
                    "\n" +
                    "/**\n" +
                    " * author : iwdael\n" +
                    " * e-mail : iwdael@outlook.com\n" +
                    " */\n" +
//                    "@Dao\n" +
                    "interface $REPLACE_CLASS_NAME {\n" +
                    "\n" +
                    "$REPLACE_FIND_ALL\n" +
                    "\n" +
                    "$REPLACE_FIND\n" +
                    "}"
    }

    override fun classFull() = "com.iwdael.dblite.${DTA.targetClassName}"
    override fun className() = DTA.targetClassName
    override fun packageName() = "com.iwdael.dblite"
    override fun make(): String {
        return CONTENT
            .replace(REPLACE_CLASS_NAME, "${DTA.targetClassName}Room")
            .replace(REPLACE_FIND,"")
            .replace(
                REPLACE_FIND,
                DTA.eClass.getVariable()
                    .joinToString(
                        prefix = "    fun find(",
                        postfix = "): ${DTA.targetClassName}",
                        separator = ", ",
                        transform = {
                            "${it.name()}: ${
                                it.type().javaFullClass2KotlinShotClass()
                            }? = null"
                        })

            )
            .replace(
                REPLACE_FIND_ALL,
                "    @Query(\"SELECT * FROM ${DTA.tableName}\")" +
                        "\n" +
                        "    fun all(): List<${DTA.targetClassName}>"
            )
            .replace(REPLACE_ENTITY, "${DTA.packageName}.${DTA.targetClassName}")
    }


}