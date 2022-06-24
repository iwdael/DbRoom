package com.iwdael.dbroom.compiler.element

import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement

class Parameter(element: Element) {
    private val e = element as  VariableElement
    val `package` = e.enclosingElement.enclosingElement.enclosingElement.asType().toString()
    val className = e.enclosingElement.enclosingElement.simpleName.toString()
    val methodName = e.enclosingElement.simpleName.toString()
    val owner = "${`package`}.${className}.${methodName}"
    val type = e.asType().toString()
    val name = e.simpleName.toString()
    override fun toString(): String {
        return "{" +
                "package:\"${`package`}\"," +
                "className:\"${`className`}\"," +
                "methodName:\"${`methodName`}\"," +
                "owner:\"${owner}\"," +
                "type:\"${type}\"," +
                "name:\"${name}\"" +
                "}"
    }
}