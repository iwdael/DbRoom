package com.iwdael.dblite.compiler.e

import javax.lang.model.element.Element

/**
 * author : Iwdael
 * e-mail : iwdael@outlook.com
 */
class EVariable(val element: Element) {


    override fun toString() = "" +
            "annotationMirrors:${element.annotationMirrors.joinToString()}\n" +
            "simpleName:${element.simpleName}\n" +
            "kind:${element.kind}\n" +
            "asType:${element.asType()}\n" +
            "enclosingElement:${element.enclosingElement}\n"

    fun type() = "${element.asType()}"
    fun name() = "${element.simpleName}"

}