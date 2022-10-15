package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.dbroom.annotations.UseDataBinding

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */

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


fun Class.observerPackage(): String {
    return packageName() + ".room"
}

fun Class.observerClassName(): String {
    return observerPackage() + ".${classSimpleName}Observer"
}


fun List<Class>.useDataBinding(): Boolean {
    return any { it.getAnnotation(UseDataBinding::class.java) != null }
}