package com.iwdael.dbroom.compiler.compat

import androidx.room.ColumnInfo
import com.iwdael.dbroom.compiler.element.Field
import com.squareup.javapoet.JavaFile
import javax.annotation.processing.Filer

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
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
        this.name
    else this.getAnnotation(ColumnInfo::class.java)!!.name
}