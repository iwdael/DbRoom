package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */

fun Class.roomPackage(): String {
    return this.packet.name + ""
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
    return packageName() + ""
}

fun Class.observerClassName(): String {
    return observerPackage() + ".${classSimpleName}Observable"
}


fun Class.useDataBinding(): Boolean {
    return getAnnotation(UseDataBinding::class.java) != null
}

fun Class.useRoomNotifier(): Boolean {
    return getAnnotation(UseRoomNotifier::class.java) != null
}