package com.iwdael.dbroom.compiler.element

import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
class Package(element: Element) {

    private val e = element as PackageElement
    val name = e.toString()
    val annotation = e.annotationMirrors.map { Annotation(it) }
    override fun toString(): String {
        return "{" +
                "name:\"${name}\"," +
                "annotation:${annotation}" +
                "}"
    }


}