package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.annotation.UseRoomNotifier
import com.iwdael.dbroom.compiler.Generator

class RoomMapHandler(val gens: List<Generator>) {
    fun handle() {
        gens
            .filter { it.clazz.getAnnotation(UseRoomNotifier::class.java) != null }
            .forEach {
                if (it.clazz.getAnnotation(Metadata::class.java) == null)
                    RoomMapJavaHandler(it).handle()
                else
                    RoomMapKotlinHandler(it).handle()
            }
    }
}
