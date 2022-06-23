package com.iwdael.dbroom.compiler.e

import androidx.room.ColumnInfo
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
    fun colName(): String {
        val info: ColumnInfo = element.getAnnotation(ColumnInfo::class.java) ?: return name()
        if (info.name == ColumnInfo.INHERIT_FIELD_NAME) return name()
        return info.name
    }

}