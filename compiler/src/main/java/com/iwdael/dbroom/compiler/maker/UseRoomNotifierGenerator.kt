package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.annotations.UseRoomNotifier

class UseRoomNotifierGenerator(private val classes: List<Class>) {
    fun handle() {
        classes
            .filter { it.getAnnotation(UseRoomNotifier::class.java) != null }
            .forEach {
                if (it.getAnnotation(Metadata::class.java) == null)
                    UseRoomNotifierJavaGenerator(it).handle()
                else
                    UseRoomNotifierKotlinGenerator(it).handle()
            }
    }
}
