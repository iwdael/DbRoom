package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import com.iwdael.annotationprocessorparser.Class

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class Generator(val clazz: Class) {
    val packageName = clazz.`package`.name
    val packageNameGenerator = clazz.`package`.name + ".room"
    val cn = clazz.name
    val classNameGenerator = cn + "Room"
    val tableName =
        if (clazz.getAnnotation(Entity::class.java)?.tableName?.isNotEmpty() == true) "`" + clazz.getAnnotation(
            Entity::class.java
        )!!.tableName + "`"
        else "`$cn`"

    val fields = clazz.fields.filter { it.getAnnotation(Ignore::class.java) == null }
    val methods = clazz.methods
}