package com.iwdael.dbroom.compiler.compat

import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.dbroom.compiler.primaryKey
import com.iwdael.dbroom.compiler.roomFields

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */

fun Class.getUpdateFiled(): Pair<Field, List<Field>> {
    return this.primaryKey() to
            this.roomFields().filter { it.getAnnotation(PrimaryKey::class.java) == null }
}
