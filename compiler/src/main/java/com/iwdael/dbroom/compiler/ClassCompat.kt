package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asClassName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

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

fun Class.columnClassName(): ClassName {
    return "${this.className}Column".asTypeName().asClassName()
}

fun Class.interfaceColumnClassName(): ClassName {
    return "${this.className}Column.Column".asTypeName().asClassName()
}

fun Class.sqlClassName(): ClassName {
    return "${this.className}SQL".asTypeName().asClassName()
}

fun Class.sqlQueryBuilderClassName(): ClassName {
    return "${this.className}SQL.QueryBuilder".asTypeName().asClassName()
}

fun Class.sqlQueryClassName(): ClassName {
    return "${this.className}SQL.Query".asTypeName().asClassName()
}


fun Class.whereBuilderClassName(): ClassName {
    return "${this.className}ConditionBuilder".asTypeName().asClassName()
}

fun Class.whereBuilder2ClassName(): ClassName {
    return "${this.className}ConditionBuilder2".asTypeName().asClassName()
}

fun Field.columnClassName(): ClassName {
    val name = when (asTypeName()) {
        TypeName.BOOLEAN -> JavaClass.BASIC_BOOLEAN_COLUMN.simpleName()
        JavaClass.BOOLEAN_PACKING -> JavaClass.PACKING_BOOLEAN_COLUMN.simpleName()
        TypeName.BYTE -> JavaClass.BASIC_BYTE_COLUMN.simpleName()
        JavaClass.BYTE_PACKING -> JavaClass.PACKING_BYTE_COLUMN.simpleName()
        TypeName.CHAR -> JavaClass.BASIC_CHAR_COLUMN.simpleName()
        JavaClass.CHAR_PACKING -> JavaClass.PACKING_CHAR_COLUMN.simpleName()
        TypeName.DOUBLE -> JavaClass.BASIC_DOUBLE_COLUMN.simpleName()
        JavaClass.DOUBLE_PACKING -> JavaClass.PACKING_DOUBLE_COLUMN.simpleName()
        TypeName.FLOAT -> JavaClass.BASIC_FLOAT_COLUMN.simpleName()
        JavaClass.FLOAT_PACKING -> JavaClass.PACKING_FLOAT_COLUMN.simpleName()
        TypeName.INT -> JavaClass.BASIC_INT_COLUMN.simpleName()
        JavaClass.INT_PACKING -> JavaClass.PACKING_INT_COLUMN.simpleName()
        TypeName.LONG -> JavaClass.BASIC_LONG_COLUMN.simpleName()
        JavaClass.LONG_PACKING -> JavaClass.PACKING_LONG_COLUMN.simpleName()
        TypeName.SHORT -> JavaClass.BASIC_SHORT_COLUMN.simpleName()
        JavaClass.SHORT_PACKING -> JavaClass.PACKING_SHORT_COLUMN.simpleName()
        ClassName.get(String::class.java) -> JavaClass.PACKING_STRING_COLUMN.simpleName()
        else -> JavaClass.PACKING_STRING_COLUMN.simpleName()
    }
    return ClassName.get(
        "${parent.columnClassName().packageName()}.${
            parent.columnClassName().simpleName()
        }", name
    )
}

fun Field.whereClassName(): ClassName {
    return when (asTypeName()) {
        TypeName.BOOLEAN -> JavaClass.WHERE_BOOLEAN_BASIC
        JavaClass.BOOLEAN_PACKING -> JavaClass.WHERE_BOOLEAN_PACKING
        TypeName.BYTE -> JavaClass.WHERE_BYTE_BASIC
        JavaClass.BYTE_PACKING -> JavaClass.WHERE_BYTE_PACKING
        TypeName.CHAR -> JavaClass.WHERE_CHAR_BASIC
        JavaClass.CHAR_PACKING -> JavaClass.WHERE_CHAR_PACKING
        TypeName.DOUBLE -> JavaClass.WHERE_DOUBLE_BASIC
        JavaClass.DOUBLE_PACKING -> JavaClass.WHERE_DOUBLE_PACKING
        TypeName.FLOAT -> JavaClass.WHERE_FLOAT_BASIC
        JavaClass.FLOAT_PACKING -> JavaClass.WHERE_FLOAT_PACKING
        TypeName.INT -> JavaClass.WHERE_INTEGER_BASIC
        JavaClass.INT_PACKING -> JavaClass.WHERE_INTEGER_PACKING
        TypeName.LONG -> JavaClass.WHERE_LONG_BASIC
        JavaClass.LONG_PACKING -> JavaClass.WHERE_LONG_PACKING
        TypeName.SHORT -> JavaClass.WHERE_SHORT_BASIC
        JavaClass.SHORT_PACKING -> JavaClass.WHERE_SHORT_PACKING
        ClassName.get(String::class.java) -> JavaClass.WHERE_STRING_PACKING
        else -> JavaClass.WHERE_STRING_PACKING
    }
}