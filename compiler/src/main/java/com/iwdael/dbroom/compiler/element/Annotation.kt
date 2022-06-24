package com.iwdael.dbroom.compiler.element


import javax.lang.model.element.AnnotationMirror

class Annotation(mirror: AnnotationMirror) {
    val name = mirror.annotationType.toString()
    val values = mirror.elementValues.entries.map { it.key.simpleName to it.value }
//    override fun toString(): String {
//        return "{" +
//                "name:\"${name}\",\n" +
//                "values:\"${values}\"" +
//                "}"
//    }

    override fun toString(): String {
        return "{name:\"${name}\"}"
    }
}