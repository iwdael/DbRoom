package com.iwdael.dbroom.example

import androidx.room.TypeConverter
import com.iwdael.dbroom.example.entity.Music


@TypeConverter
fun musicConvertString(user: Music): String {
    return ""
}

@TypeConverter
fun stringConvertMusic(user: String): Music? {
    return null
}