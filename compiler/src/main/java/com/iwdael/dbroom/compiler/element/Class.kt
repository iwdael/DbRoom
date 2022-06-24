package com.iwdael.dbroom.compiler.element


import java.lang.Class
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


class Class(element: Element) {
    private val e = element as TypeElement
    val `package` = Package(e.enclosingElement)
    val name = e.simpleName.toString()
    val fields = e.enclosedElements.filter { it.kind == ElementKind.FIELD }.map { Field(it) }
    val methods = e.enclosedElements.filter { it.kind == ElementKind.METHOD }.map { Method(it) }
    val superClass = e.superclass.toString()
    val interfaces = e.interfaces.map { it.toString() }
    val annotations = e.annotationMirrors.map { Annotation(it) }

    fun <A : kotlin.Annotation> getAnnotation(clazz: Class<A>): A? {
        return e.getAnnotation(clazz)
    }

    override fun toString(): String {

        return Format().formatJson("{" +
                "package:${`package`}," +
                "name:\"${`name`}\"," +
                "fields:${`fields`}," +
                "methods:${`methods`}," +
                "superClass:\"${`superClass`}\"," +
                "interfaces:${`interfaces`}," +
                "annotations:${`annotations`}" +
                "}")
    }


}