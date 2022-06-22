package com.iwdael.dblite.compiler.e

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement


/**
 * author : Iwdael
 * e-mail : iwdael@outlook.com
 */
class EClass(val element: Element) {
    fun getPackage() = element.enclosingElement.toString()
    fun getClassName() = element.simpleName.toString()
    fun getMethod() = element.enclosedElements.filter {
        it.kind == ElementKind.METHOD
    }.map { EMethod(it as ExecutableElement) }

    fun getVariable() = element.enclosedElements.filter {
        it.kind == ElementKind.FIELD
    }.map { EVariable(it) }

    fun sourceFileIsKotlin() = element.getAnnotation(Metadata::class.java) != null

    override fun toString() = "${getVariable().joinToString()}"
}