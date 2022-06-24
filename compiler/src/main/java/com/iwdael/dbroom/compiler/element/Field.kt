package com.iwdael.dbroom.compiler.element

import java.lang.Class
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement


class Field(element: Element) {
    private val e = element as VariableElement
    val `package` = e.enclosingElement.enclosingElement.toString()
    val className = e.enclosingElement.simpleName.toString()
    val owner = "${`package`}.${className}"
    val type = e.asType().toString()
    val name = e.simpleName.toString()
    val value = e.constantValue
    val annotation = e.annotationMirrors.map { Annotation(it) }
    fun <A : kotlin.Annotation> getAnnotation(clazz: Class<A>): A? {
        return e.getAnnotation(clazz)
    }

    override fun toString(): String {
        return "{" +
                "package:\"${`package`}\"," +
                "className:\"${`className`}\"," +
                "type:\"${`type`}\"," +
                "owner:\"${`owner`}\"," +
                "name:\"${`name`}\"," +
                "value:\"${`value`}\"," +
                "annotation:${`annotation`}," +
                "}"
    }


}
