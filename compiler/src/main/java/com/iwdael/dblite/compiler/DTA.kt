package com.iwdael.dblite.compiler

import androidx.room.Entity
import com.iwdael.dblite.compiler.e.EClass

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
class DTA(val eClass: EClass) {
    val packageName = eClass.getPackage()
    val targetClassName = eClass.getClassName()
    val generatedClassName = targetClassName + "Room"
    val generatedFullClassName = "com.iwdael.dblite.${generatedClassName}"
    val tableName = eClass.element.annotationMirrors
        .firstOrNull { it.annotationType.toString().contains(Entity::class.java.name) }
        ?.elementValues
        ?.map { it.key.toString() to it.value.toString() }
        ?.firstOrNull { it.first.contains("tableName") }
        ?.second
        ?.trim { it == '\"' }
        ?: targetClassName
}