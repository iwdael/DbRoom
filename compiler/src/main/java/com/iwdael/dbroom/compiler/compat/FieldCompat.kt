package com.iwdael.dbroom.compiler.compat

import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.dbroom.annotations.Delete
import com.iwdael.dbroom.annotations.Insert
import com.iwdael.dbroom.annotations.Find
import com.iwdael.dbroom.annotations.Update
import com.iwdael.dbroom.compiler.roomFields

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
fun Class.getInsert(): List<Pair<String, List<Field>>> {
    return this.roomFields()
        .filter { it.getAnnotation(Insert::class.java) != null }
        .map { it.getAnnotation(Insert::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.roomFields().filter { it.getAnnotation(Insert::class.java) != null }
                .filter { it.getAnnotation(Insert::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Class.getDelete(): List<Pair<String, List<Field>>> {
    return this.roomFields()
        .filter { it.getAnnotation(Delete::class.java) != null }
        .map { it.getAnnotation(Delete::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.roomFields().filter { it.getAnnotation(Delete::class.java) != null }
                .filter { it.getAnnotation(Delete::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Class.getUpdate(): List<Pair<String, Pair<List<Field>, List<Field>>>> {
    return this.roomFields().filter { it.getAnnotation(Update::class.java) != null }
        .map { it.getAnnotation(Update::class.java)!! }
        .flatMap { it.value.toMutableList().apply { addAll(it.where.toList()) } }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to (this.roomFields()
                .filter { it.getAnnotation(Update::class.java) != null }
                .filter { it.getAnnotation(Update::class.java)!!.value.toSet().contains(key) } to
                    this.roomFields()
                        .filter { it.getAnnotation(Update::class.java) != null }
                        .filter {
                            it.getAnnotation(Update::class.java)!!.where.toSet().contains(key)
                        })
        }
        .filter { it.second.first.isNotEmpty() && it.second.second.isNotEmpty() }
        .toList()
}

fun Class.getQuery(): List<Pair<String, List<Field>>> {
    return this.roomFields()
        .filter { it.getAnnotation(Find::class.java) != null }
        .map { it.getAnnotation(Find::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.roomFields().filter { it.getAnnotation(Find::class.java) != null }
                .filter { it.getAnnotation(Find::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Class.getUpdateFiled(): Pair<Field, List<Field>> {
    return this.roomFields().filter { it.getAnnotation(PrimaryKey::class.java) != null }.first() to
            this.roomFields().filter { it.getAnnotation(PrimaryKey::class.java) == null }
}
