package com.iwdael.dbroom.compiler.maker

import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotation.UseRoomMap
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.*
import java.io.File
import java.lang.StringBuilder

class RoomMapHandler(val gens: List<Generator>) {
    fun handle() {
        gens
            .filter { it.clazz.getAnnotation(UseRoomMap::class.java) != null }
            .filter { it.clazz.getAnnotation(Metadata::class.java) == null }
            .forEach { gen ->
                val fileContent = StringBuilder(
                    File(gen.clazz.filePath()!!).readLines().joinToString(separator = "\n")
                )
                gen.fields.filter { it.getAnnotation(PrimaryKey::class.java) == null }
                    .map { it to gen.setter(it) }
                    .forEach {
                        val tab = "    "
                        val insertContent = "notifyPropertyChanged(DB.${it.first.name});\n"
                        if (!fileContent.contains(insertContent)) {
                            val index = fileContent.setterLastLineIndex(
                                it.first.type.simpleClassName(),
                                it.second
                            )
                            if (index > 0) fileContent.insert(index, tab + insertContent + tab)
                        }
                    }
                File(gen.clazz.filePath()!!).writeBytes(fileContent.toString().toByteArray())
            }


        gens
            .filter { it.clazz.getAnnotation(UseRoomMap::class.java) != null }
            .filter { it.clazz.getAnnotation(Metadata::class.java) != null }
            .forEach { gen ->
                val builders =
                    File(gen.clazz.filePath()!!).readLines().flatMap { it.split(";") }
                        .map { StringBuilder(it) }
                gen.fields.filter { it.getAnnotation(PrimaryKey::class.java) == null }
                    .forEach { filed ->
                        val insertContent = "\n        set(value) {\n" +
                                "            field = value\n" +
                                "            notifyPropertyChanged(DB.${filed.name})\n" +
                                "        }"
                        if (!builders.map { it.toString().replace(" ", "") }
                                .any { it == "notifyPropertyChanged(DB.${filed.name})" }) {
                            builders.filter {
                                val line = it.toString().replace(" ", "")
                                line.contains("var${filed.name}:") ||
                                        line.contains("val${filed.name}:") ||
                                        line.contains("var${filed.name}=") ||
                                        line.contains("val${filed.name}=")
                            }.forEach { it.append(insertContent) }
                        }
                    }
                val fileContent = builders.joinToString("\n")
                File(gen.clazz.filePath()!!).writeBytes(fileContent.toString().toByteArray())
            }

    }
}