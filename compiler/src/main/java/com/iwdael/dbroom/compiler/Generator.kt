package com.iwdael.dbroom.compiler

import androidx.room.Entity
import com.iwdael.dbroom.compiler.element.Class

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
class Generator(val clazz: Class) {
    val packageName = clazz.`package`.name
    val packageNameGenerator = clazz.`package`.name + ".room"
    val className = clazz.name
    val classNameGenerator = className + "Room"
    val tableName =
        if (clazz.getAnnotation(Entity::class.java)?.tableName?.isEmpty() == true) className
        else clazz.getAnnotation(Entity::class.java)?.tableName
}