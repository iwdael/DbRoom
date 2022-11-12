package com.iwdael.dbroom.compiler.compat

import androidx.room.ColumnInfo
import com.iwdael.annotationprocessorparser.Field
import com.squareup.javapoet.JavaFile
import javax.annotation.processing.Filer

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
fun JavaFile.write(filer: Filer) {
    try {
        this.writeTo(filer)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Field.colName(): String {
    return if (this.getAnnotation(ColumnInfo::class.java) == null || this.getAnnotation(ColumnInfo::class.java)!!.name == ColumnInfo.INHERIT_FIELD_NAME)
        "`" + this.name + "`"
    else "`" + this.getAnnotation(ColumnInfo::class.java)!!.name + "`"
}