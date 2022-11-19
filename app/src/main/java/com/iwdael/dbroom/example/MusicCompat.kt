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
    val query = MusicSQL.newQuery()
        .fields(MusicColumn.lyrics)
        .where(MusicColumn.name)
        .equal("")
        .build()
    println(query.selection)
}