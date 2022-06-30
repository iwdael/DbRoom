package com.iwdael.dbroom.compiler.compat

import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.Method


fun List<Method>.getField(field: Field): Method {
    return this.filter { it.parameter.isEmpty() }
        .filter { it.`return` == field.type }
        .firstOrNull { it.name.contains(field.name, true) }
        ?: throw Exception("getter method not found:${field.owner}.${field.name}")
}