package com.iwdael.dbroom.example.util

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.annotations.EnableCoroutines
import com.iwdael.dbroom.example.entity.Music

/**
 * @author 段泽全
 * @since 2024/11/27
 * @desc this is RoomUtils
 */
object RoomUtils {
    @TypeConverter
    fun musicConvertString(user: Music?): String? {
        return ""
    }

    @TypeConverter
    fun stringConvertMusic(user: String): Music? {
        return null
    }

    @DbRoomCreator(version = 3, exportSchema = false)
    @EnableCoroutines(true)
    fun <T : RoomDatabase> create(context: Context, room: Class<T>): T {
        return Room.databaseBuilder(context, room, "DbRoom.db").build()
    }
}