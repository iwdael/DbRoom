package com.iwdael.dbroom.compiler.element


import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class Method(element: Element) {
    private val e = element as ExecutableElement
    val `package` = e.enclosingElement.enclosingElement.toString()
    val className = e.enclosingElement.simpleName.toString()
    val owner = "${`package`}.${className}"
    val name = e.simpleName.toString()
    val parameter = e.parameters.map { Parameter(it) }
    val `return` = e.returnType.toString()
    override fun toString(): String {
        return "{" +
                "package:\"${`package`}\"," +
                "className:\"${className}\"," +
                "owner:\"${owner}\"," +
                "name:\"${name}\"," +
                "parameter:${parameter}," +
                "return:${`return`}" +
                "}"
    }

}