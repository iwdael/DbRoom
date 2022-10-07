package com.iwdael.dbroom.compiler.compat

import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.dbroom.annotation.Delete
import com.iwdael.dbroom.annotation.Insert
import com.iwdael.dbroom.annotation.Find
import com.iwdael.dbroom.annotation.Update
import com.iwdael.dbroom.compiler.Generator


fun List<Method>.getField(field: Field): Method {
    return this.filter { it.parameter.isEmpty() }
        .filter { it.`return` == field.type }
        .firstOrNull { it.name.contains(field.name, true) }
        ?: throw Exception("getter method not found:${field.owner}.${field.name}")
}

fun Generator.getInsert(): List<Pair<String, List<Field>>> {
    return this.fields
        .filter { it.getAnnotation(Insert::class.java) != null }
        .map { it.getAnnotation(Insert::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.fields.filter { it.getAnnotation(Insert::class.java) != null }
                .filter { it.getAnnotation(Insert::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Generator.getDelete(): List<Pair<String, List<Field>>> {
    return this.fields
        .filter { it.getAnnotation(Delete::class.java) != null }
        .map { it.getAnnotation(Delete::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.fields.filter { it.getAnnotation(Delete::class.java) != null }
                .filter { it.getAnnotation(Delete::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Generator.getUpdate(): List<Pair<String, Pair<List<Field>, List<Field>>>> {
    return this.fields.filter { it.getAnnotation(Update::class.java) != null }
        .map { it.getAnnotation(Update::class.java)!! }
        .flatMap { it.value.toMutableList().apply { addAll(it.where.toList()) } }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to (this.fields
                .filter { it.getAnnotation(Update::class.java) != null }
                .filter { it.getAnnotation(Update::class.java)!!.value.toSet().contains(key) } to
                    this.fields
                        .filter { it.getAnnotation(Update::class.java) != null }
                        .filter {
                            it.getAnnotation(Update::class.java)!!.where.toSet().contains(key)
                        })
        }
        .filter { it.second.first.isNotEmpty() && it.second.second.isNotEmpty() }
        .toList()
}

fun Generator.getQuery(): List<Pair<String, List<Field>>> {
    return this.fields
        .filter { it.getAnnotation(Find::class.java) != null }
        .map { it.getAnnotation(Find::class.java)!! }
        .flatMap { it.value.toSet() }
        .toHashSet()
        .toMutableList()
        .map { key ->
            key to this.fields.filter { it.getAnnotation(Find::class.java) != null }
                .filter { it.getAnnotation(Find::class.java)!!.value.toSet().contains(key) }
        }
        .filter { it.second.isNotEmpty() }
        .toList()
}

fun Generator.getUpdateFiled(): Pair<Field, List<Field>> {
    return this.fields.filter { it.getAnnotation(PrimaryKey::class.java) != null }.first() to
            this.fields.filter { it.getAnnotation(PrimaryKey::class.java) == null }
}

fun Generator.getter(field: Field): String {
    val methods = methods
        .filter { it.parameter.isEmpty() }
        .filter { it.`return` == field.type }
        .filter {
            it.name.toLowerCase() == "get${field.name.toLowerCase()}" ||
                    it.name.equals(field.name, ignoreCase = true) ||
                    it.name.replace("get", "")
                        .equals(field.name.replace("is", ""), ignoreCase = true)

        }
    if (methods.size != 1) throw Exception("Can not find getter for  ${field.owner}.${field.name}")
    return methods.first().name
}


fun Generator.setter(field: Field): String {
    val methods = methods
        .filter { it.parameter.size == 1 }
        .filter { it.parameter[0].type  == field.type }
        .filter {
            it.name.toLowerCase() == "set${field.name.toLowerCase()}" ||
                    it.name.equals(field.name, ignoreCase = true) ||
                    it.name.replace("set", "")
                        .equals(field.name.replace("is", ""), ignoreCase = true)
        }
    if (methods.size != 1) throw Exception("Can not find setter for  ${field.owner}.${field.name}")
    return methods.first().name
}
