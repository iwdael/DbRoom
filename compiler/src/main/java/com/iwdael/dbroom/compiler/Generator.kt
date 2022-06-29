package com.iwdael.dbroom.compiler

import androidx.room.Entity
import com.iwdael.annotationprocessorparser.Class
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
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