package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class Generator(val clazz: Class) {
    val packageName = clazz.packet.name
    val roomPackage = clazz.packet.name + ".room"
    val classSimpleName = clazz.classSimpleName
    val roomSimpleClassName = classSimpleName + "Room"
    val roomTableName =
        if (clazz.getAnnotation(Entity::class.java)?.tableName?.isNotEmpty() == true) "`" + clazz.getAnnotation(
            Entity::class.java
        )!!.tableName + "`"
        else "`$classSimpleName`"

    val roomFields = clazz.fields.filter { it.getAnnotation(Ignore::class.java) == null }
    val roomPrimaryKeyField by lazy {
        clazz.fields.firstOrNull { it.getAnnotation(PrimaryKey::class.java) != null }
            ?: throw Exception("Can not found @PrimaryKey:${clazz.className}")
    }
}


fun Class.roomPackage(): String {
    return this.packet.name + ".room"
}

fun Class.roomSimpleClassName(): String {
    return this.classSimpleName + "Room"
}

fun Class.roomClassName(): String {
    return roomPackage() + "." + roomSimpleClassName()
}

fun Class.roomTableName(): String {
    return if (getAnnotation(Entity::class.java)?.tableName?.isNotEmpty() == true) "`" + getAnnotation(
        Entity::class.java
    )!!.tableName + "`"
    else "`$classSimpleName`"
}

fun Class.roomFields(): List<Field> {
    return fields.filter { it.getAnnotation(Ignore::class.java) == null }
}

fun Class.roomPrimaryKeyField(): Field {
    return roomFields().firstOrNull { it.getAnnotation(PrimaryKey::class.java) != null }
        ?: throw Exception("Can not found @PrimaryKey:${className}")
}

fun Class.packageName(): String {
    return packet.name
}

fun Class.classSimpleName(): String {
    return classSimpleName
}