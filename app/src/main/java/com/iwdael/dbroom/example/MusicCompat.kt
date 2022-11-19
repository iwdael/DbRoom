package com.iwdael.dbroom.example

import androidx.room.TypeConverter
import com.iwdael.dbroom.example.entity.Music
import com.iwdael.dbroom.example.entity.MusicColumn
import com.iwdael.dbroom.example.entity.MusicSQL


@TypeConverter
fun musicConvertString(user: Music): String {
    return ""
}

@TypeConverter
fun stringConvertMusic(user: String): Music? {
    return null
}

fun main() {
    val query = MusicSQL.QueryBuilder()
        .fields()
        .where(MusicColumn.id)
        .greater(10)
        .and()
        .where(MusicColumn.id)
        .`in`(100,null ,null)
        .build()
    println(query.selection)
}