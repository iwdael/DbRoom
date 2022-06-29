package com.iwdael.dbroom.example

import androidx.room.TypeConverter


@TypeConverter
fun usertoString(user: User): String {
    return ""
}

@TypeConverter
fun stringConverterUser(user: String): User? {
    return null
}