package com.iwdael.dbroom.compiler

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asClassName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseNotifier
import com.iwdael.dbroom.annotations.UseRoom
import com.iwdael.dbroom.compiler.compat.bestGuessClassName
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

fun Class.primaryKey(): Field {
    if (this.roomFields().none { it.getAnnotation(PrimaryKey::class.java) != null }) {
        throw Exception("Can not found PrimaryKey(${this.className})")
    }
    val field = roomFields().first { it.getAnnotation(PrimaryKey::class.java) != null }
    if (field.asTypeName()::class.java == TypeName::class.java) {
        throw Exception("PrimaryKey cannot be a basic type(${this.className}.${field.name})")
    }
    return field
}

fun Class.packageName(): String {
    return packet.name
}


fun Class.notifierClassName(): ClassName {
    return "${className}Notifier".bestGuessClassName().asClassName()
}


fun Class.useDataBinding(): Boolean {
    return getAnnotation(UseDataBinding::class.java) != null
}

fun Class.useRoom(): Boolean {
    return getAnnotation(UseRoom::class.java) != null
}

fun Class.useNotifier(): Boolean {
    return getAnnotation(UseNotifier::class.java) != null
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

fun Class.sqlFinderBuilderClassName(): ClassName {
    return "${this.className}SQL.FindBuilder".asTypeName().asClassName()
}

fun Class.sqlFinderClassName(): ClassName {
    return "${this.className}SQL.Finder".asTypeName().asClassName()
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
        TypeName.BOOLEAN -> JavaClass.CONDITION_BOOLEAN_BASIC
        JavaClass.BOOLEAN_PACKING -> JavaClass.CONDITION_BOOLEAN_PACKING
        TypeName.BYTE -> JavaClass.CONDITION_BYTE_BASIC
        JavaClass.BYTE_PACKING -> JavaClass.CONDITION_BYTE_PACKING
        TypeName.CHAR -> JavaClass.CONDITION_CHAR_BASIC
        JavaClass.CHAR_PACKING -> JavaClass.CONDITION_CHAR_PACKING
        TypeName.DOUBLE -> JavaClass.CONDITION_DOUBLE_BASIC
        JavaClass.DOUBLE_PACKING -> JavaClass.CONDITION_DOUBLE_PACKING
        TypeName.FLOAT -> JavaClass.CONDITION_FLOAT_BASIC
        JavaClass.FLOAT_PACKING -> JavaClass.CONDITION_FLOAT_PACKING
        TypeName.INT -> JavaClass.CONDITION_INTEGER_BASIC
        JavaClass.INT_PACKING -> JavaClass.CONDITION_INTEGER_PACKING
        TypeName.LONG -> JavaClass.CONDITION_LONG_BASIC
        JavaClass.LONG_PACKING -> JavaClass.CONDITION_LONG_PACKING
        TypeName.SHORT -> JavaClass.CONDITION_SHORT_BASIC
        JavaClass.SHORT_PACKING -> JavaClass.CONDITION_SHORT_PACKING
        ClassName.get(String::class.java) -> JavaClass.CONDITION_STRING_PACKING
        else -> JavaClass.CONDITION_STRING_PACKING
    }
}