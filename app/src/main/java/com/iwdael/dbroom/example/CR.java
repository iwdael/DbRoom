package com.iwdael.dbroom.example;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.iwdael.dbroom.annotations.DbRoomCreator;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public class CR {
    @DbRoomCreator
    public static <T extends RoomDatabase> T createRoomDatabase(Context context, Class<T> room) {
        return Room.databaseBuilder(context, room, "DbRoom.db").build();
    }
}
