package com.iwdael.dbroom.example

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.example.entity.Music


@TypeConverter
fun musicConvertString(user: Music): String {
    return ""
}

@TypeConverter
fun stringConvertMusic(user: String): Music? {
    return null
}

//@DbRoomCreator
//fun <T : RoomDatabase> create(context: Context, room: Class<T>): T {
//    return Room.databaseBuilder(context, room, "DbRoom.db").build()
//}