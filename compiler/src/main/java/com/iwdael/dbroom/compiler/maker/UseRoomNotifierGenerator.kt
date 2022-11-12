package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.annotations.UseRoomNotifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class UseRoomNotifierGenerator(private val classes: List<Class>) {
    fun generate() {
        classes
            .filter { it.getAnnotation(UseRoomNotifier::class.java) != null }
            .forEach {
                if (it.getAnnotation(Metadata::class.java) == null)
                    UseRoomNotifierJavaGenerator(it).generate()
                else
                    UseRoomNotifierKotlinGenerator(it).generate()
            }
    }
}
