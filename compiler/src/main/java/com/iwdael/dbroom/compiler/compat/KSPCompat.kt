package com.iwdael.dbroom.compiler.compat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.iwdael.kotlinsymbolprocessor.KSPClass
import com.iwdael.kotlinsymbolprocessor.KSPProperty
import com.iwdael.kotlinsymbolprocessor.asTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */


fun KSPClass.roomClassName(): String {
    return kspPackage.name + "." + this.simpleName + "Room"
}


fun KSPClass.tableName(): String {
    return if (annotation(Entity::class)?.tableName?.isNotEmpty() == true) "`" + annotation(
        Entity::class
    )!!.tableName + "`"
    else "`$simpleName`"
}

fun KSPClass.columns(): List<KSPProperty> {
    return kspProperties
        .filter { it.annotation(Ignore::class) == null }
}

fun KSPClass.primaryKey(): KSPProperty {
    if (this.columns().none { it.annotation(PrimaryKey::class) != null }) {
        throw Exception("Can not found PrimaryKey(${this.qualifierName})")
    }
    val field = columns().first { it.annotation(PrimaryKey::class) != null }
    if (field.type.asTypeName()::class.java == TypeName::class.java) {
        throw Exception("PrimaryKey cannot be a basic type(${this.qualifierName}.${field.name})")
    }
    return field
}

fun KSPClass.getUpdateFiled(): Pair<KSPProperty, List<KSPProperty>> {
    return this.primaryKey() to
            this.columns().filter { it.annotation(PrimaryKey::class) == null }
}
fun KSPProperty.colName(): String {
    if (this.annotation(ColumnInfo::class) == null ||
        this.annotation(ColumnInfo::class)?.name == ColumnInfo.INHERIT_FIELD_NAME
    ) {
        return "`" + this.name + "`"
    } else {
        return "`" + this.annotation(ColumnInfo::class)!!.name + "`"
    }
}
